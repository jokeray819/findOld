<template>
  <view class="tag-picker">
    <view class="label" v-if="label">{{ label }}</view>
    <view class="tags-wrap">
      <view
        v-for="t in tags"
        :key="t.id"
        class="tag-item"
        :class="{ active: selectedIds.includes(t.id) }"
        @tap="toggle(t.id)"
      >
        {{ t.name }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  tags: { type: Array, default: () => [] },
  modelValue: { type: Array, default: () => [] },
  label: { type: String, default: '' },
})

const emit = defineEmits(['update:modelValue'])

const selectedIds = computed({
  get: () => props.modelValue || [],
  set: (val) => emit('update:modelValue', val),
})

function toggle(id) {
  const arr = [...(props.modelValue || [])]
  const i = arr.indexOf(id)
  if (i >= 0) arr.splice(i, 1)
  else arr.push(id)
  emit('update:modelValue', arr)
}
</script>

<style lang="scss" scoped>
.tag-picker { margin-bottom: 24rpx; }
.label { font-size: 28rpx; color: #666; margin-bottom: 12rpx; }
.tags-wrap { display: flex; flex-wrap: wrap; gap: 16rpx; }
.tag-item {
  padding: 12rpx 24rpx;
  border-radius: 32rpx;
  font-size: 26rpx;
  background: #f0f0f0;
  color: #666;
  &.active { background: #e8f4fd; color: #07c160; }
}
</style>
