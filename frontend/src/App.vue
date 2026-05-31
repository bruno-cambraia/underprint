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
        {{ resource.status === 'ESGOTADA' ? '🪨 Esgotada' : '⛏️ Minerar' }}
      </button>

      <div class="bar-label">Durabilidade</div>
      <div class="bar">
        <div class="bar-fill green" :style="{ width: durabilityPercent + '%' }"></div>
      </div>

      <template v-if="resource.status === 'ESGOTADA'">
        <div class="bar-label">Respawn {{ respawnCountdown.toFixed(1) }}s</div>
        <div class="bar">
          <div class="bar-fill red" :style="{ width: respawnPercent + '%' }"></div>
        </div>
      </template>
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
const TOTAL_RESPAWN = 5

const resource = ref<ResourceState>({
  name: 'rock',
  durability: 10,
  resourceCount: 0,
  status: 'DISPONIVEL',
  secondsUntilRespawn: 0
})

const respawnCountdown = ref(0)

const durabilityPercent = computed(() => (resource.value.durability / 10) * 100)
const respawnPercent = computed(() => (respawnCountdown.value / TOTAL_RESPAWN) * 100)

async function fetchState() {
  const res = await fetch(`${API}/resource/rock`)
  const data = await res.json()
  resource.value = data
  if (data.status === 'ESGOTADA') {
    respawnCountdown.value = data.secondsUntilRespawn
  }
}

async function mine() {
  const res = await fetch(`${API}/resource/rock/mine`, { method: 'POST' })
  const data = await res.json()
  resource.value = data
  if (data.status === 'ESGOTADA') {
    respawnCountdown.value = TOTAL_RESPAWN
  }
}

let fetchInterval: number
let countdownInterval: number

onMounted(() => {
  fetchState()
  fetchInterval = setInterval(() => {
    if (resource.value.status === 'ESGOTADA') fetchState()
  }, 1000)
  countdownInterval = setInterval(() => {
    if (respawnCountdown.value > 0) {
      respawnCountdown.value = Math.max(0, respawnCountdown.value - 0.1)
    }
  }, 100)
})

onUnmounted(() => {
  clearInterval(fetchInterval)
  clearInterval(countdownInterval)
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
  gap: 0.5rem;
  margin-top: 2rem;
}

.rock-info { text-align: center; }

.mine-btn {
  padding: 1rem 2rem;
  font-size: 1.5rem;
  cursor: pointer;
  border: none;
  border-radius: 8px;
  background-color: #4a4a4a;
  color: white;
  transition: transform 0.1s;
  margin-bottom: 0.5rem;
}

.mine-btn:hover:not(:disabled) { transform: scale(1.05); }
.mine-btn:active:not(:disabled) { transform: scale(0.95); }
.mine-btn:disabled { background-color: #999; cursor: not-allowed; }

.bar-label {
  font-size: 0.8rem;
  color: #666;
  align-self: flex-start;
  margin-left: calc(50% - 100px);
}

.bar {
  width: 200px;
  height: 15px;
  background-color: #ddd;
  border-radius: 6px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  transition: width 0.1s linear;
}

.green { background-color: #4caf50; }
.red { background-color: #f44336; }
</style>