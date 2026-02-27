<template>
  <div>
    <h2 class="title">Dashboard Overview</h2>

    <div class="cards">
      <div class="card">
        <p>Total Games</p>
        <h3>1,240</h3>
      </div>

      <div class="card">
        <p>Total Products</p>
        <h3>3,560</h3>
      </div>

      <div class="card">
        <p>Total Sales</p>
        <h3>$12,840</h3>
      </div>

      <div class="card">
        <p>Active Users</p>
        <h3>892</h3>
      </div>
    </div>
  </div>
</template>

<style scoped>
.title {
  margin-bottom: 20px;
  font-size: 22px;
}

.cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.card {
  background: #0f172a;
  padding: 20px;
  border-radius: 15px;
  border: 1px solid #1e293b;
  transition: 0.3s;
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 0 20px rgba(59,130,246,0.2);
}

.card p {
  color: #94a3b8;
  font-size: 14px;
}

.card h3 {
  margin-top: 10px;
  font-size: 24px;
}
</style>