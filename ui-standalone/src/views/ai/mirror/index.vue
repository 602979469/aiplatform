<template>
  <div class="mirror-page">
    <!-- Google 风格首页 -->
    <div v-if="!searched" class="hero">
      <div class="hero-logo">🐳</div>
      <h1 class="hero-title">Docker镜像加速器</h1>
      <p class="hero-sub">输入镜像名称搜索，自动匹配当前设备架构</p>
      <div class="hero-input-wrap">
        <el-input
          v-model="searchInput"
          class="hero-input"
          placeholder="请输入镜像名称，如 mysql:8、openjdk:17"
          clearable
          @keyup.enter.native="handleSearch"
        />
      </div>
      <div class="hero-actions">
        <el-button type="primary" class="hero-btn" :loading="searching" @click="handleSearch">搜 索</el-button>
      </div>
      <div class="client-info hero-client">
        当前设备：<el-tag size="mini" type="info">{{ client.os }}</el-tag>
        <el-tag size="mini" type="info">{{ client.arch }}</el-tag>
        <span class="client-tip">镜像会优先匹配当前架构，可通过加速器（docker.xuanyuan.run）拉取</span>
      </div>
    </div>

    <!-- 搜索结果页 -->
    <div v-else class="app-container mirror-container">
      <div class="search-panel">
        <div class="search-row">
          <el-input
            v-model="searchInput"
            class="search-input"
            size="medium"
            placeholder="请输入镜像名称，如 mysql:8、openjdk:17，不写版本默认 latest"
            clearable
            @keyup.enter.native="handleSearch"
          />
          <el-button type="primary" size="medium" :loading="searching" @click="handleSearch">搜索</el-button>
          <el-button size="medium" plain @click="backHome">返回首页</el-button>
        </div>
        <div class="client-info">
          当前设备：<el-tag size="mini" type="info">{{ client.os }}</el-tag>
          <el-tag size="mini" type="info">{{ client.arch }}</el-tag>
          <span class="client-tip">镜像会优先匹配当前架构，可通过加速器（docker.xuanyuan.run）拉取</span>
        </div>
      </div>

      <!-- 结果列表 -->
      <el-table v-loading="searching" :data="results" border stripe empty-text="没有找到相关镜像，换个名称试试">
        <el-table-column label="厂商" prop="vendor" width="130" align="center" />
        <el-table-column label="镜像完整名称" prop="fullName" min-width="260" show-overflow-tooltip />
        <el-table-column label="版本号" prop="tag" width="160" show-overflow-tooltip />
        <el-table-column label="架构" prop="arch" width="140" align="center" />
        <el-table-column label="本地文件" width="90" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.localFileExists" type="success" size="mini">已有</el-tag>
            <el-tag v-else type="info" size="mini">无</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" align="center">
          <template slot-scope="{ row }">
            <el-button v-if="row.localFileExists" type="success" size="mini" icon="el-icon-download" @click="handleDownload(row)">下载</el-button>
            <div v-else-if="row.taskStatus === 'generating'" class="generating-cell">
              <span class="downloading-text">下载中<span class="dots"><i>.</i><i>.</i><i>.</i></span> {{ row.progressMsg || ('进度 ' + row.progress + '%') }}</span>
            </div>
            <el-tooltip v-else-if="row.taskStatus === 'failed'" :content="row.errorMsg || '生成失败'" placement="top">
              <el-button type="danger" size="mini" plain icon="el-icon-refresh" @click="handleGenerate(row)">失败，重试</el-button>
            </el-tooltip>
            <el-button v-else type="primary" size="mini" icon="el-icon-link" @click="handleGenerate(row)">生成下载链接</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="results.length > 0" class="result-tip">
        共找到 {{ results.length }} 个镜像，本地没有的镜像点击「生成下载链接」后会自动拉取并打包。
      </div>
    </div>
  </div>
</template>

<script>
import { searchMirror, generateDownload, getDownloadStatus, downloadMirrorFile } from '@/api/ai/mirror'

export default {
  name: 'AiMirror',
  data() {
    return {
      searchInput: '',
      searching: false,
      searched: false,
      results: [],
      client: {
        os: '未知',
        arch: 'amd64'
      },
      pollTimers: {}
    }
  },
  created() {
    this.detectClientInfo()
  },
  beforeDestroy() {
    Object.values(this.pollTimers).forEach(timer => clearInterval(timer))
  },
  methods: {
    /** 检测客户端 OS 与 CPU 架构 */
    detectClientInfo() {
      const ua = navigator.userAgent || ''
      let os = '未知'
      if (/windows/i.test(ua)) os = 'Windows'
      else if (/mac os|macintosh/i.test(ua)) os = 'macOS'
      else if (/android/i.test(ua)) os = 'Android'
      else if (/iphone|ipad/i.test(ua)) os = 'iOS'
      else if (/linux/i.test(ua)) os = 'Linux'
      this.client.os = os

      const platform = navigator.platform || ''
      if (/arm64|aarch64/i.test(ua + ' ' + platform)) {
        this.client.arch = 'arm64'
      } else if (/x86_64|win64|amd64/i.test(ua + ' ' + platform)) {
        this.client.arch = 'amd64'
      }
      if (navigator.userAgentData && navigator.userAgentData.getHighEntropyValues) {
        navigator.userAgentData.getHighEntropyValues(['architecture']).then(values => {
          const arch = (values.architecture || '').toLowerCase()
          if (arch === 'x86') this.client.arch = 'amd64'
          else if (arch === 'arm') this.client.arch = 'arm64'
          else if (arch) this.client.arch = arch
        }).catch(() => {})
      }
    },

    /** 搜索镜像 */
    handleSearch() {
      const imageName = this.searchInput.trim()
      if (!imageName) {
        this.$message.warning('请输入镜像名称')
        return
      }
      this.searched = true
      this.searching = true
      this.results = []
      searchMirror({
        imageName: imageName,
        os: this.client.os,
        arch: this.client.arch
      }).then(response => {
        const data = response.data || {}
        this.results = data.results || []
        if (data.os) this.client.os = data.os
        if (data.arch) this.client.arch = data.arch
        if (this.results.length === 0) {
          this.$message.warning('没有找到相关镜像，换个名称试试')
        }
      }).finally(() => {
        this.searching = false
      })
    },

    /** 返回首页 */
    backHome() {
      this.searched = false
      this.results = []
      this.searchInput = ''
    },

    /** 生成下载链接 */
    handleGenerate(row) {
      this.$confirm(`将为镜像「${row.fullName}」执行 docker pull 并打包成 tar（可能需要几分钟），是否继续？`, '生成下载链接', {
        confirmButtonText: '开始生成',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$set(row, 'taskStatus', 'generating')
        this.$set(row, 'progress', 5)
        this.$set(row, 'progressMsg', '任务创建中')
        this.$set(row, 'errorMsg', '')
        this.$set(row, 'errorCode', '')
        generateDownload({
          repo: row.repo,
          tag: row.tag,
          arch: this.client.arch
        }).then(response => {
          const task = response.data || {}
          if (task.status === 'ready') {
            this.$set(row, 'taskStatus', 'ready')
            this.$set(row, 'progress', 100)
            this.$set(row, 'localFileExists', true)
            this.$message.success('本地已有该镜像文件，可直接下载')
          } else {
            this.$set(row, 'taskId', task.taskId)
            this.pollStatus(row)
          }
        }).catch(() => {
          this.$set(row, 'taskStatus', null)
        })
      }).catch(() => {})
    },

    /** 轮询生成进度 */
    pollStatus(row) {
      if (this.pollTimers[row.taskId]) {
        clearInterval(this.pollTimers[row.taskId])
      }
      this.pollTimers[row.taskId] = setInterval(() => {
        getDownloadStatus(row.taskId).then(response => {
          const task = response.data || {}
          this.$set(row, 'progress', task.progress)
          this.$set(row, 'progressMsg', task.progressMsg)
          if (task.status === 'ready') {
            clearInterval(this.pollTimers[row.taskId])
            delete this.pollTimers[row.taskId]
            this.$set(row, 'taskStatus', 'ready')
            this.$set(row, 'localFileExists', true)
            this.$message.success('下载链接生成完成')
          } else if (task.status === 'failed') {
            clearInterval(this.pollTimers[row.taskId])
            delete this.pollTimers[row.taskId]
            this.$set(row, 'taskStatus', 'failed')
            this.$set(row, 'errorCode', task.errorCode)
            this.$set(row, 'errorMsg', task.errorMsg || '生成失败')
            this.$message.error(row.errorMsg)
          }
        }).catch(() => {})
      }, 1500)
    },

    /** 下载 tar 文件 */
    handleDownload(row) {
      const fileName = row.localFileName
      if (!fileName) {
        this.$message.warning('缺少文件名')
        return
      }
      downloadMirrorFile(fileName).then(response => {
        const blob = new Blob([response])
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      }).catch(() => {
        this.$message.error('下载失败，请重试')
      })
    }
  }
}
</script>

<style scoped lang="scss">
.mirror-page {
  min-height: calc(100vh - 110px);
  display: flex;
  flex-direction: column;
}

.hero {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-bottom: 12vh;

  .hero-logo {
    font-size: 96px;
    line-height: 1;
  }

  .hero-title {
    margin: 20px 0 8px;
    font-size: 46px;
    font-weight: 600;
    letter-spacing: 2px;
    color: #303133;
  }

  .hero-sub {
    margin: 0 0 30px;
    font-size: 14px;
    color: #909399;
  }

  .hero-input-wrap {
    width: 660px;
    max-width: 92vw;
  }

  .hero-input {
    .el-input__inner {
      height: 50px;
      line-height: 50px;
      font-size: 16px;
      border-radius: 25px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
    }
  }

  .hero-actions {
    margin-top: 24px;
  }

  .hero-btn {
    width: 150px;
    height: 46px;
    font-size: 16px;
    border-radius: 23px;
  }

  .hero-client {
    margin-top: 28px;
  }
}

.mirror-container {
  .search-panel {
    margin-bottom: 16px;
  }

  .search-row {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .search-input {
    max-width: 520px;
  }

  .client-info {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 10px;
    font-size: 13px;
    color: #909399;
  }

  .client-tip {
    margin-left: 6px;
    color: #c0c4cc;
  }

  .generating-cell {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
  }

  .downloading-text {
    color: #409eff;
    font-size: 12px;
    line-height: 24px;

    .dots i {
      font-style: normal;
      animation: dot-blink 1.2s infinite;
    }

    .dots i:nth-child(2) {
      animation-delay: 0.2s;
    }

    .dots i:nth-child(3) {
      animation-delay: 0.4s;
    }
  }

  .result-tip {
    margin-top: 12px;
    font-size: 12px;
    color: #909399;
  }
}

@keyframes dot-blink {
  0%,
  20% {
    opacity: 0;
  }
  40% {
    opacity: 1;
  }
  100% {
    opacity: 1;
  }
}
</style>
