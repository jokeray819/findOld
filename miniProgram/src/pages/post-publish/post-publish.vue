<template>
  <view class="page">
    <view class="form">
      <view class="field">
        <text class="label">物品类型 *</text>
        <picker mode="selector" :range="typeOptions" range-key="label" @change="onTypeChange">
          <view class="picker-value">{{ typeLabel }}</view>
        </picker>
      </view>
      <view class="field">
        <text class="label">年代</text>
        <picker mode="selector" :range="eraOptions" range-key="label" @change="onEraChange">
          <view class="picker-value">{{ eraLabel }}</view>
        </picker>
      </view>
      <view class="field">
        <text class="label">详细描述 *</text>
        <textarea
          v-model="form.description"
          placeholder="外观、材质、年代、使用场景等"
          class="textarea"
          maxlength="1000"
        />
      </view>
      <view class="field">
        <text class="label">赏金方式</text>
        <picker mode="selector" :range="bountyModeOptions" range-key="label" @change="onBountyModeChange">
          <view class="picker-value">{{ bountyModeLabel }}</view>
        </picker>
      </view>
      <view class="field" v-if="form.bountyMode && form.bountyMode !== 'none'">
        <text class="label">赏金金额（元）</text>
        <input v-model.number="form.bountyAmount" type="digit" placeholder="选填" class="input" />
      </view>
      <view class="field">
        <text class="label">快速标签</text>
        <tag-picker v-model="form.tagIds" :tags="allTags" />
      </view>
      <view class="field">
        <text class="label">封面图 URL</text>
        <input v-model="form.coverImage" placeholder="可选，图片链接" class="input" />
      </view>
      <view class="field">
        <text class="label">手绘/草图 URL</text>
        <input v-model="form.sketchImage" placeholder="可选" class="input" />
      </view>
      <view class="actions">
        <button class="btn draft" @tap="submit('draft')">存草稿</button>
        <button class="btn primary" @tap="submit('open')">发布</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { createPost, updatePost, getPost } from '@/api/post.js'
import { createBountyOrder } from '@/api/bounty.js'
import { listTags } from '@/api/tag.js'
import { useUserStore } from '@/store/user.js'

const postId = ref(0)
const allTags = ref([])
const form = ref({
  type: 'toy',
  description: '',
  sceneEra: '',
  regionZone: '',
  bountyAmount: null,
  bountyMode: 'none',
  status: 'open',
  coverImage: '',
  sketchImage: '',
  tagIds: [],
})

const typeOptions = [
  { value: 'toy', label: '玩具' },
  { value: 'stationery', label: '文具' },
  { value: 'snack', label: '零食' },
  { value: 'old_object', label: '老物件' },
]
const eraOptions = [
  { value: '', label: '不选' },
  { value: '80s', label: '80后' },
  { value: '90s', label: '90后' },
  { value: '00s', label: '00后' },
]
const bountyModeOptions = [
  { value: 'none', label: '无赏金' },
  { value: 'escrow', label: '先托管（平台担保）' },
  { value: 'post_pay', label: '后付（双方协商）' },
]

const typeLabel = computed(() => typeOptions.find((x) => x.value === form.value.type)?.label || '请选择')
const eraLabel = computed(() => eraOptions.find((x) => x.value === form.value.sceneEra)?.label || '不选')
const bountyModeLabel = computed(() => bountyModeOptions.find((x) => x.value === form.value.bountyMode)?.label || '无赏金')

onLoad((q) => {
  postId.value = Number(q?.id) || 0
})

onMounted(async () => {
  try {
    allTags.value = await listTags() || []
  } catch (_) {}
  if (postId.value) {
    try {
      const p = await getPost(postId.value)
      form.value = {
        type: p.type || 'toy',
        description: p.description || '',
        sceneEra: p.sceneEra || '',
        regionZone: p.regionZone || '',
        bountyAmount: p.bountyAmount ?? null,
        bountyMode: p.bountyMode || 'none',
        status: p.status || 'open',
        coverImage: p.coverImage || '',
        sketchImage: p.sketchImage || '',
        tagIds: (p.tags || []).map((t) => t.id),
      }
    } catch (_) {}
  }
})

function onTypeChange(e) {
  const i = Number(e.detail?.value ?? 0)
  form.value.type = typeOptions[i].value
}
function onEraChange(e) {
  const i = Number(e.detail?.value ?? 0)
  form.value.sceneEra = eraOptions[i].value
}
function onBountyModeChange(e) {
  const i = Number(e.detail?.value ?? 0)
  form.value.bountyMode = bountyModeOptions[i].value
}

async function submit(status) {
  const { type, description } = form.value
  if (!description?.trim()) {
    uni.showToast({ title: '请填写描述', icon: 'none' })
    return
  }
  const userStore = useUserStore()
  if (!userStore.isLoggedIn) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  const payload = {
    type,
    description: description.trim(),
    sceneEra: form.value.sceneEra || undefined,
    regionZone: form.value.regionZone || undefined,
    bountyAmount: form.value.bountyAmount > 0 ? form.value.bountyAmount : undefined,
    bountyMode: form.value.bountyMode || 'none',
    status,
    coverImage: form.value.coverImage || undefined,
    sketchImage: form.value.sketchImage || undefined,
    tagIds: form.value.tagIds?.length ? form.value.tagIds : undefined,
  }
  try {
    if (postId.value) {
      await updatePost(postId.value, payload)
      uni.showToast({ title: '已更新' })
    } else {
      const res = await createPost(payload)
      uni.showToast({ title: '发布成功' })
      if (payload.bountyMode && payload.bountyMode !== 'none' && payload.bountyAmount > 0 && res?.id) {
        try {
          await createBountyOrder({ postId: res.id, amount: payload.bountyAmount })
        } catch (_) {}
      }
    }
    setTimeout(() => {
      uni.navigateBack()
    }, 800)
  } catch (_) {}
}
</script>

<style lang="scss" scoped>
.page { padding: 24rpx; background: #f5f5f5; min-height: 100vh; }
.form { background: #fff; border-radius: 16rpx; padding: 24rpx; }
.field { margin-bottom: 28rpx; }
.label { display: block; font-size: 28rpx; color: #333; margin-bottom: 12rpx; }
.picker-value { padding: 20rpx; background: #f5f5f5; border-radius: 12rpx; font-size: 28rpx; }
.textarea { width: 100%; min-height: 180rpx; padding: 20rpx; border: 1rpx solid #eee; border-radius: 12rpx; box-sizing: border-box; }
.input { width: 100%; padding: 20rpx; border: 1rpx solid #eee; border-radius: 12rpx; box-sizing: border-box; }
.actions { display: flex; gap: 24rpx; margin-top: 40rpx; }
.btn { flex: 1; height: 88rpx; line-height: 88rpx; border-radius: 12rpx; font-size: 30rpx; border: none; }
.btn.draft { background: #f0f0f0; color: #666; }
.btn.primary { background: #07c160; color: #fff; }
</style>
