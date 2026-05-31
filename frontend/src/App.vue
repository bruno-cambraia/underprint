<template>
  <div class="game">
    <h1>Underprint</h1>

    <div class="scene">
      <div class="rock-wrapper">
        <img
            src="/rock.png"
            class="rock-img"
            :class="{ hit: isHitting, exhausted: resource.status === 'ESGOTADA' }"
            @click="mine"
        />
      </div>

      <div class="info">
        <p>⛏️ Pedras brutas: {{ resource.resourceCount }}</p>
      </div>

      <div class="bars">
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

const isHitting = ref(false)
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
  if (resource.value.status === 'ESGOTADA') return

  isHitting.value = true
  setTimeout(() => isHitting.value = false, 150)

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
  min-height: 100vh;
  background-image: url('/background.jpeg');
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  font-family: 'Georgia', serif;
}

h1 {
  color: #f0e6c8;
  font-size: 2.5rem;
  text-shadow: 2px 2px 8px #000;
  margin-bottom: 1rem;
}

.scene {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.rock-wrapper {
  cursor: pointer;
}

.rock-img {
  width: 220px;
  transition: transform 0.1s, filter 0.1s;
  filter: drop-shadow(0 8px 16px rgba(0,0,0,0.8));
}

.rock-img:hover {
  transform: scale(1.05);
}

.rock-img.hit {
  transform: scale(0.92) rotate(-3deg);
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.8)) brightness(1.3);
}

.rock-img.exhausted {
  filter: grayscale(80%) drop-shadow(0 4px 8px rgba(0,0,0,0.5));
  cursor: not-allowed;
  opacity: 0.5;
}

.info {
  color: #f0e6c8;
  font-size: 1.2rem;
  text-shadow: 1px 1px 4px #000;
  background: rgba(0,0,0,0.5);
  padding: 0.5rem 1rem;
  border-radius: 8px;
}

.bars {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  background: rgba(0,0,0,0.6);
  padding: 1rem;
  border-radius: 10px;
  min-width: 220px;
}

.bar-label {
  font-size: 0.8rem;
  color: #f0e6c8;
}

.bar {
  width: 100%;
  height: 12px;
  background-color: rgba(255,255,255,0.2);
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