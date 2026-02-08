<template>
  <view class="page">
    <view v-if="!list.length && !loading" class="empty">暂无收藏</view>
    <scroll-view
      v-else
      scroll-y
      class="list-scroll"
      :style="{ height: scrollHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view class="list-inner">
        <post-card v-for="p in list" :key="p.id" :post="p" />
        <view v-if="loading" class="tip">加载中...</view>
        <view v-else-if="noMore && list.length" class="tip">没有更多了</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listMyFavorites } from '@/api/favorite.js'

const list = ref([])
const page = ref(0)
const size = 20
const loading = ref(false)
const noMore = ref(false)
const scrollHeight = ref(400)

async function fetchList() {
  if (loading.value) return
  loading.value = true
  try {
    const res = await listMyFavorites(page.value, size)
    const content = res.content || []
    if (page.value === 0) list.value = content
    else list.value = list.value.concat(content)
    noMore.value = res.last !== undefined ? res.last : list.value.length >= (res.totalElements ?? 0)
  } catch (_) {}
  finally {
    loading.value = false
  }
}

function loadMore() {
  if (loading.value || noMore.value) return
  page.value += 1
  fetchList()
}

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  scrollHeight.value = (sys.windowHeight || 400) - 100
  fetchList()
})
</script>

<style lang="scss" scoped>
.page { background: #f5f5f5; min-height: 100vh; }
.empty { padding: 80rpx; text-align: center; color: #999; }
.list-scroll { padding: 24rpx; }
.list-inner { padding-bottom: 48rpx; }
.tip { text-align: center; padding: 24rpx; color: #999; font-size: 26rpx; }
</style>
