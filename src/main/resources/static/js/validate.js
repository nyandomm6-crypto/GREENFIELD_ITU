document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("signupForm");
  const btn = document.getElementById("btn");
  if (!form || !btn) {
    return;
  }

  const inputs = form.querySelectorAll("input[name]");

  async function validate() {
    const data = {};
    inputs.forEach((i) => {
      if (i.name) data[i.name] = i.value;
    });

    try {
      const res = await fetch("/validation", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams(data),
      });

      if (!res.ok) {
        throw new Error("Erreur serveur: " + res.status);
      }

      const errors = await res.json();
      let hasError = false;

      inputs.forEach((i) => i.classList.remove("is-invalid"));
      form.querySelectorAll(".text-danger.small").forEach((e) => {
        e.textContent = "";
      });

      for (const key in errors) {
        if (!Object.prototype.hasOwnProperty.call(errors, key)) {
          continue;
        }
        hasError = true;
        const input = form.querySelector(`[name="${key}"]`);
        const error = document.getElementById("err-" + key);
        if (input) input.classList.add("is-invalid");
        if (error) error.textContent = "⚠ " + errors[key];
      }

      btn.disabled = hasError;
    } catch (err) {
      console.error("Erreur de validation:", err);
    }
  }

  inputs.forEach((i) => i.addEventListener("input", validate));
  setTimeout(validate, 100);
});
