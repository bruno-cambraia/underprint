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
          :disabled="resource.status === 'ESGOTADA' || cooldown > 0"
          @click="mine"
      >
        {{ cooldown > 0 ? `⏳ ${cooldown.toFixed(1)}s` : resource.status === 'ESGOTADA' ? '🪨 Esgotada' : '⛏️ Minerar' }}
      </button>

      <div class="bar-label">Durabilidade</div>
      <div class="bar">
        <div class="bar-fill green" :style="{ width: durabilityPercent + '%' }"></div>
      </div>

      <div v-if="resource.status === 'ESGOTADA'" class="bar-label">Respawn</div>
      <div v-if="resource.status === 'ESGOTADA'" class="bar">
        <div class="bar-fill red" :style="{ width: respawnPercent + '%' }"></div>
      </div>

      <div v-if="cooldown > 0" class="bar-label">Cooldown</div>
      <div v-if="cooldown > 0" class="bar">
        <div class="bar-fill orange" :style="{ width: cooldownPercent + '%' }"></div>
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
const TOTAL_RESPAWN = 5
const COOLDOWN_TIME = 0.5

const resource = ref<ResourceState>({
  name: 'rock',
  durability: 10,
  resourceCount: 0,
  status: 'DISPONIVEL',
  secondsUntilRespawn: 0
})

const cooldown = ref(0)

const durabilityPercent = computed(() => (resource.value.durability / 10) * 100)
const respawnPercent = computed(() => (resource.value.secondsUntilRespawn / TOTAL_RESPAWN) * 100)
const cooldownPercent = computed(() => (cooldown.value / COOLDOWN_TIME) * 100)

async function fetchState() {
  const res = await fetch(`${API}/resource/rock`)
  resource.value = await res.json()
}

async function mine() {
  if (cooldown.value > 0) return
  const res = await fetch(`${API}/resource/rock/mine`, { method: 'POST' })
  resource.value = await res.json()
  cooldown.value = COOLDOWN_TIME
}

let interval: number
let cooldownInterval: number

onMounted(() => {
  fetchState()
  interval = setInterval(() => {
    if (resource.value.status === 'ESGOTADA') fetchState()
  }, 1000)
  cooldownInterval = setInterval(() => {
    if (cooldown.value > 0) {
      cooldown.value = Math.max(0, cooldown.value - 0.1)
    }
  }, 100)
})

onUnmounted(() => {
  clearInterval(interval)
  clearInterval(cooldownInterval)
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
  height: 12px;
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
.orange { background-color: #ff9800; }
</style>