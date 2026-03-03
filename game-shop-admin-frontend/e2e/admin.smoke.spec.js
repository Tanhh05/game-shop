import { test, expect } from "@playwright/test"

// Set env before run:
// E2E_ADMIN_USER=admin
// E2E_ADMIN_PASS=123456

test("admin login and navigate core pages", async ({ page }) => {
  await page.goto("/login")

  await page.getByPlaceholder("Username").fill(process.env.E2E_ADMIN_USER || "")
  await page.getByPlaceholder("Password").fill(process.env.E2E_ADMIN_PASS || "")
  await page.getByRole("button", { name: /đăng nhập/i }).click()

  await expect(page).toHaveURL(/\/admin\/dashboard/)

  await page.getByRole("link", { name: /games/i }).click()
  await expect(page).toHaveURL(/\/admin\/games/)

  await page.getByRole("link", { name: /products/i }).click()
  await expect(page).toHaveURL(/\/admin\/products/)

  await page.getByRole("link", { name: /orders/i }).click()
  await expect(page).toHaveURL(/\/admin\/orders/)

  await page.getByRole("link", { name: /users/i }).click()
  await expect(page).toHaveURL(/\/admin\/users/)

  await page.getByRole("link", { name: /inventory/i }).click()
  await expect(page).toHaveURL(/\/admin\/inventory/)
})
