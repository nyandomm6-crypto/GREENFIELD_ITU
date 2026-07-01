document.addEventListener("DOMContentLoaded", function () {
  var boutonsQuestion = document.querySelectorAll(".faq-question");

  boutonsQuestion.forEach(function (bouton) {
    bouton.addEventListener("click", function () {
      var item = bouton.closest(".faq-item");
      if (!item) {
        return;
      }

      var etaitOuvert = item.classList.contains("ouvert");

      // Optionnel : referme les autres questions quand on en ouvre une
      // nouvelle, pour garder l'affichage propre. Commenter ce bloc si
      // vous préférez pouvoir garder plusieurs réponses ouvertes en
      // même temps.
      document.querySelectorAll(".faq-item.ouvert").forEach(function (autre) {
        autre.classList.remove("ouvert");
      });

      if (!etaitOuvert) {
        item.classList.add("ouvert");
      }
    });
  });
});
