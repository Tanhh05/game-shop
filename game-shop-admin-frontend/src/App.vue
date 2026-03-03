<template>
  <router-view />
</template>

<script setup>
</script>

<style>
@import url("https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap");

:root {
  color-scheme: light;
  --bg: #f7f4ee;
  --bg-alt: #f1efe7;
  --surface: #ffffff;
  --surface-2: #f6f6f3;
  --surface-3: #efeee8;
  --ink: #17181a;
  --muted: #6b6f76;
  --line: #e4e0d7;
  --accent: #0ea5a4;
  --accent-2: #f59e0b;
  --accent-3: #1f6feb;
  --danger: #ef4444;
  --success: #16a34a;
  --shadow: 0 20px 60px rgba(22, 24, 26, 0.08);
  --shadow-soft: 0 10px 30px rgba(22, 24, 26, 0.06);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: "Plus Jakarta Sans", "Segoe UI", sans-serif;
  background: radial-gradient(circle at 20% 20%, #ffffff, var(--bg));
  color: var(--ink);
}

a {
  color: inherit;
}

button,
input,
select,
textarea {
  font-family: inherit;
}

.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.page-subtitle {
  color: var(--muted);
  font-size: 14px;
}

.card {
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 16px;
  padding: 18px;
  box-shadow: var(--shadow-soft);
}

.card.soft {
  background: var(--surface-2);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.metric-value {
  font-size: 26px;
  font-weight: 700;
  margin-top: 8px;
}

.metric-label {
  color: var(--muted);
  font-size: 13px;
}

.table-wrap {
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-soft);
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table thead th {
  text-align: left;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--muted);
  background: var(--surface-2);
  padding: 14px 16px;
}

.table td {
  padding: 14px 16px;
  border-top: 1px solid var(--line);
  font-size: 14px;
}

.table tbody tr:hover {
  background: #faf9f5;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.badge.success { background: rgba(22, 163, 74, 0.12); color: #12833a; }
.badge.danger { background: rgba(239, 68, 68, 0.12); color: #c81e1e; }
.badge.warning { background: rgba(245, 158, 11, 0.16); color: #b45309; }
.badge.neutral { background: rgba(15, 23, 42, 0.08); color: #1f2937; }

.btn {
  border: 1px solid transparent;
  border-radius: 10px;
  padding: 8px 14px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: var(--accent);
  color: #fff;
  box-shadow: 0 10px 20px rgba(14, 165, 164, 0.2);
}

.btn-primary:hover { transform: translateY(-1px); }

.btn-outline {
  background: transparent;
  border-color: var(--line);
  color: var(--ink);
}

.btn-ghost {
  background: var(--surface-2);
  border-color: var(--surface-3);
  color: var(--ink);
}

.btn-danger {
  background: rgba(239, 68, 68, 0.12);
  color: #b91c1c;
  border-color: rgba(239, 68, 68, 0.2);
}

.input,
.textarea,
.select {
  width: 100%;
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 10px 12px;
  background: var(--surface);
  color: var(--ink);
  font-size: 14px;
}

.input::placeholder,
.textarea::placeholder {
  color: #9aa0a6;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.helper {
  color: var(--muted);
  font-size: 13px;
}

.error {
  color: #b91c1c;
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.2);
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 13px;
  white-space: pre-line;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 14px;
  color: var(--muted);
}

@media (max-width: 1100px) {
  .metrics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 700px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>
