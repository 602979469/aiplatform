<template>
  <i :class="'svg-icon el-icon-' + iconClass" />
</template>

<script>
export default {
  name: 'SvgIcon',
  props: {
    iconClass: {
      type: String,
      required: true
    },
    className: {
      type: String,
      default: ''
    }
  }
}
</script>
