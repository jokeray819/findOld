<template>
  <view class="post-card" @tap="goDetail">
    <view v-if="post.coverImage" class="cover-wrap">
      <image class="cover" :src="post.coverImage" mode="aspectFill" />
    </view>
    <view class="body">
      <view class="type-era">
        <text class="type-tag">{{ typeLabel }}</text>
        <text v-if="post.sceneEra" class="era-tag">{{ sceneEraLabel }}</text>
        <text v-if="post.bountyMode && post.bountyMode !== 'none'" class="bounty-tag">赏金</text>
      </view>
      <text class="desc">{{ post.description }}</text>
      <view class="tags" v-if="post.tags && post.tags.length">
        <text v-for="t in post.tags" :key="t.id" class="tag">{{ t.name }}</text>
      </view>
      <view class="footer">
        <text class="answer-count">{{ post.answerCount || 0 }} 回答</text>
        <text class="time">{{ timeStr }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  post: {
    type: Object,
    required: true,
  },
})

const typeMap = {
  toy: '玩具',
  stationery: '文具',
  snack: '零食',
  old_object: '老物件',
}
const eraMap = { '80s': '80后', '90s': '90后', '00s': '00后' }

const typeLabel = computed(() => typeMap[props.post.type] || props.post.type || '')
const sceneEraLabel = computed(() => eraMap[props.post.sceneEra] || props.post.sceneEra || '')
const timeStr = computed(() => formatTime(props.post.createdAt))

function formatTime(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 86400000 * 7) return Math.floor(diff / 86400000) + '天前'
  return d.getMonth() + 1 + '-' + d.getDate()
}

function goDetail() {
  uni.navigateTo({
    url: `/pages/post-detail/post-detail?id=${props.post.id}`,
  })
}
</script>

<style lang="scss" scoped>
.post-card {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 24rpx;
}
.cover-wrap {
  height: 280rpx;
  background: #f0f0f0;
}
.cover {
  width: 100%;
  height: 100%;
}
.body { padding: 24rpx; }
.type-era {
  margin-bottom: 12rpx;
  .type-tag, .era-tag, .bounty-tag {
    font-size: 22rpx;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    margin-right: 12rpx;
  }
  .type-tag { background: #e8f4fd; color: #07c160; }
  .era-tag { background: #fff3e0; color: #f90; }
  .bounty-tag { background: #ffe8e8; color: #e54; }
}
.desc {
  font-size: 28rpx;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.tags {
  margin-top: 12rpx;
  .tag {
    font-size: 22rpx;
    color: #999;
    margin-right: 16rpx;
  }
}
.footer {
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #999;
  .answer-count { margin-right: 24rpx; }
}
</style>
