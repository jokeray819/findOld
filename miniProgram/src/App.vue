<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user.js'

onLaunch(() => {
  const userStore = useUserStore()
  userStore.initFromStorage()
})

onShow(() => {})
onHide(() => {})
</script>

<style lang="scss">
@import './static/styles/common.scss';
</style>
