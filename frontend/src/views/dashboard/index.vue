<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { NCard, NStatistic, NGrid, NGridItem } from 'naive-ui'

const user = useUserStore()

onMounted(() => {
  user.fetchMe()
})
</script>

<template>
  <div class="p-6">
    <h2 class="text-2xl mb-4">欢迎回来，{{ user.username || '...' }}</h2>
    <NGrid :cols="3" :x-gap="16">
      <NGridItem>
        <NCard>
          <NStatistic label="故事数量" :value="0" />
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard>
          <NStatistic label="角色数量" :value="0" />
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard>
          <NStatistic label="会话数量" :value="0" />
        </NCard>
      </NGridItem>
    </NGrid>
  </div>
</template>
