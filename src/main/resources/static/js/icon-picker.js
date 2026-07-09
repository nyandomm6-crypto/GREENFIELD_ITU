document.addEventListener("DOMContentLoaded", function () {
  const iconPicker = document.querySelector(".icon-picker");
  if (!iconPicker) {
    return;
  }

  const hiddenInput = iconPicker.querySelector("input[type=hidden]#icon");
  const preview = iconPicker.querySelector("#iconPickerPreview");
  const label = iconPicker.querySelector("#iconPickerLabel");
  const buttons = iconPicker.querySelectorAll(".icon-picker-item");

  function setIcon(value) {
    if (!hiddenInput || !preview || !label) {
      return;
    }

    hiddenInput.value = value;
    preview.className = "fs-4";
    if (value) {
      preview.classList.add(...value.split(" "));
      label.textContent = value.replace("bi bi-", "").replace(/-/g, " ");
    } else {
      label.textContent = "Sélectionner une icône";
    }

    buttons.forEach((button) => {
      button.classList.toggle("selected", button.dataset.value === value);
    });
  }

  buttons.forEach((button) => {
    button.addEventListener("click", function () {
      setIcon(this.dataset.value);
    });
  });

  if (hiddenInput.value) {
    setIcon(hiddenInput.value);
  }
});
