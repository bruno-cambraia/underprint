<template>
  <div class="game">
    <h1>Underprint</h1>

    <div class="rock-container">
      <div class="rock-info">
        <h2>{{ resource.name }}</h2>
        <p>Durabilidade: {{ resource.durability }} / 10</p>
        <p>Pedras brutas: {{ resource.resourceCount }}</p>
      </div>

      <button
          class="mine-btn"
          :disabled="resource.status === 'ESGOTADA'"
          @click="mine"
      >
        {{ resource.status === 'ESGOTADA' ? `Esgotada (${resource.secondsUntilRespawn}s)` : '⛏️ Minerar' }}
      </button>

      <div class="durability-bar">
        <div
            class="durability-fill"
            :style="{ width: durabilityPercent + '%' }"
        ></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

interface ResourceState {
  name: string
  durability: number
  resourceCount: number
  status: string
  secondsUntilRespawn: number
}

const API = 'https://underprint.onrender.com'

const resource = ref<ResourceState>({
  name: 'rock',
  durability: 10,
  resourceCount: 0,
  status: 'DISPONIVEL',
  secondsUntilRespawn: 0
})

const durabilityPercent = computed(() => {
  return (resource.value.durability / 10) * 100
})

async function fetchState() {
  const res = await fetch(`${API}/resource/rock`)
  resource.value = await res.json()
}

async function mine() {
  const res = await fetch(`${API}/resource/rock/mine`, {
    method: 'POST'
  })
  resource.value = await res.json()
}

let interval: number

onMounted(() => {
  fetchState()
  interval = setInterval(() => {
    if (resource.value.status === 'ESGOTADA') {
      fetchState()
    }
  }, 1000)
})

onUnmounted(() => {
  clearInterval(interval)
})
</script>

<style scoped>
.game {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  font-family: sans-serif;
}

.rock-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.rock-info {
  text-align: center;
}

.mine-btn {
  padding: 1rem 2rem;
  font-size: 1.5rem;
  cursor: pointer;
  border: none;
  border-radius: 8px;
  background-color: #4a4a4a;
  color: white;
  transition: transform 0.1s;
}

.mine-btn:hover:not(:disabled) {
  transform: scale(1.05);
}

.mine-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.mine-btn:disabled {
  background-color: #999;
  cursor: not-allowed;
}

.durability-bar {
  width: 200px;
  height: 12px;
  background-color: #ddd;
  border-radius: 6px;
  overflow: hidden;
}

.durability-fill {
  height: 100%;
  background-color: #4caf50;
  transition: width 0.3s;
}
</style>