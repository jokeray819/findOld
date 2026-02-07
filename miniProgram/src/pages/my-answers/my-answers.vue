<template>
  <view class="page">
    <view v-if="!list.length && !loading" class="empty">暂无解答</view>
    <scroll-view
      v-else
      scroll-y
      class="list-scroll"
      :style="{ height: scrollHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view class="list-inner">
        <view v-for="a in list" :key="a.id" class="answer-card" @tap="goPost(a.postId)">
          <view class="content">{{ a.contentPublic }}</view>
          <view class="meta">帖子 {{ a.postId }} · {{ formatTime(a.createdAt) }}</view>
        </view>
        <view v-if="loading" class="tip">加载中...</view>
        <view v-else-if="noMore && list.length" class="tip">没有更多了</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyAnswers } from '@/api/user.js'

const list = ref([])
const page = ref(0)
const size = 20
const loading = ref(false)
const noMore = ref(false)
const scrollHeight = ref(400)

function formatTime(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate()
}

async function fetchList() {
  if (loading.value) return
  loading.value = true
  try {
    const res = await getMyAnswers(page.value, size)
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

function goPost(postId) {
  uni.navigateTo({ url: `/pages/post-detail/post-detail?id=${postId}` })
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
.answer-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  .content { font-size: 28rpx; color: #333; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
  .meta { font-size: 24rpx; color: #999; margin-top: 12rpx; }
}
.tip { text-align: center; padding: 24rpx; color: #999; font-size: 26rpx; }
</style>
