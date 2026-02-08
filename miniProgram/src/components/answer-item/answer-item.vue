<template>
  <view class="answer-item">
    <view class="content-public">{{ answer.contentPublic }}</view>
    <view v-if="answer.contentPrivate" class="content-private">
      <text class="label">仅求助者可见：</text>
      <text>{{ answer.contentPrivate }}</text>
    </view>
    <view class="footer">
      <text class="time">{{ timeStr }}</text>
      <button
        v-if="showAccept"
        class="accept-btn"
        size="mini"
        @tap="$emit('accept')"
      >
        采纳
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  answer: { type: Object, required: true },
  showAccept: { type: Boolean, default: false },
})

defineEmits(['accept'])

const timeStr = computed(() => {
  const iso = props.answer.createdAt
  if (!iso) return ''
  const d = new Date(iso)
  return (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + String(d.getMinutes()).padStart(2, '0')
})
</script>

<style lang="scss" scoped>
.answer-item {
  background: #f9f9f9;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}
.content-public { font-size: 28rpx; color: #333; line-height: 1.5; }
.content-private {
  margin-top: 12rpx;
  padding-top: 12rpx;
  border-top: 1rpx dashed #eee;
  font-size: 26rpx;
  color: #666;
  .label { color: #999; }
}
.footer {
  margin-top: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .time { font-size: 24rpx; color: #999; }
  .accept-btn { background: #07c160; color: #fff; border: none; }
}
</style>
