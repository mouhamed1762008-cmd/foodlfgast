const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read orders", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("orders").get());
});

test("Public user: can read products and categories", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(unauthDb.collection("products").get());
  await assertSucceeds(unauthDb.collection("categories").get());
});

test("Public user: cannot write products", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("products").doc("p1").set({
    name: "Burger",
    price: 500
  }));
});

test("Authenticated user: can create and read own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    uid: ALICE_UID,
    email: "alice@example.com",
    fullName: "Alice Foodie"
  }));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read or write another user's profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).set({
    uid: BOB_UID,
    email: "bob@example.com"
  }));
});

test("Authenticated user: can place valid order and query own orders", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("orders").doc("order_1").set({
    id: "order_1",
    userId: ALICE_UID,
    customerName: "Alice",
    items: [{ productId: "p1", name: "Classic Burger", price: 899, quantity: 1 }],
    total: 899,
    subtotal: 899,
    orderStatus: "PLACED",
    paymentMethod: "CASH_ON_DELIVERY"
  }));

  // Alice queries her own orders
  await assertSucceeds(aliceDb.collection("orders").where("userId", "==", ALICE_UID).get());
});

test("Authenticated user: cannot read another user's order", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("bob_order").set({
      id: "bob_order",
      userId: BOB_UID,
      items: [{ productId: "p1", name: "Fries", price: 299, quantity: 1 }],
      total: 299,
      orderStatus: "PLACED",
      paymentMethod: "CASH_ON_DELIVERY"
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("orders").doc("bob_order").get());
});
