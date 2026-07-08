document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll(".faq-item").forEach(function (item) {
    const question = item.querySelector(".faq-question");
    if (!question) return;

    question.addEventListener("click", function () {
      item.classList.toggle("ouvert");
    });
  });
});
