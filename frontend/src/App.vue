<template>
  <div class="game">
    <header class="header">
      <span class="title">Underprint</span>
      <span class="counter">Pedras brutas: {{ resource.resourceCount }}</span>
    </header>

    <div class="scene">
      <img src="/background.jpeg" class="background-img" />
      <img
          src="/rock.png"
          class="rock-img"
          :class="{ hit: isHitting, exhausted: resource.status === 'ESGOTADA' }"
          @click="mine"
      />
      <div class="respawn-bar-wrapper" v-if="resource.status === 'ESGOTADA'">
        <div class="respawn-bar">
          <div class="respawn-fill" :style="{ width: respawnPercent + '%' }"></div>
        </div>
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
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.game {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #1a1a1a;
}

.header {
  height: 56px;
  background: #111;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
  border-bottom: 1px solid #333;
  flex-shrink: 0;
}

.title {
  color: #f0e6c8;
  font-family: 'Georgia', serif;
  font-size: 1.4rem;
  letter-spacing: 2px;
}

.counter {
  color: #f0e6c8;
  font-family: 'Georgia', serif;
  font-size: 1rem;
}

.scene {
  position: relative;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.background-img {
  width: 100%;
  height: auto;
  display: block;
}

.rock-img {
  position: absolute;
  width: 23%;
  top: 42%;
  left: 44%;
  cursor: pointer;
  transition: transform 0.1s, filter 0.1s;
  filter: drop-shadow(0 2px 2px rgba(0,0,0,0.8));
}

.rock-img:hover {
  transform: scale(1.01);
}

.rock-img.hit {
  transform: scale(0.99) rotate(-1deg);
}

.rock-img.exhausted {
  filter: grayscale(80%);
  cursor: not-allowed;
}

.respawn-bar-wrapper {
  position: absolute;
  top: 64%;
  left: 50%;
  width: 23%;
}

.respawn-bar {
  width: 50%;
  height: 3px;
  background: rgba(255,255,255,0.1);
  border-radius: 6px;
  overflow: hidden;
}

.respawn-fill {
  height: 100%;
  background: #f44336;
  transition: width 0.1s linear;
}
</style>