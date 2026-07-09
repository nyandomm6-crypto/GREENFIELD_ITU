(function ($) {
  "use strict";

  // Spinner
  var spinner = function () {
    setTimeout(function () {
      if ($("#spinner").length > 0) {
        $("#spinner").removeClass("show");
      }
    }, 1);
  };
  spinner(0);

  // Fixed Navbar
  $(window).scroll(function () {
    if ($(window).width() < 992) {
      if ($(this).scrollTop() > 55) {
        $(".fixed-top").addClass("shadow");
      } else {
        $(".fixed-top").removeClass("shadow");
      }
    } else {
      if ($(this).scrollTop() > 55) {
        $(".fixed-top").addClass("shadow").css("top", -55);
      } else {
        $(".fixed-top").removeClass("shadow").css("top", 0);
      }
    }
  });

  // Back to top button
  $(window).scroll(function () {
    if ($(this).scrollTop() > 300) {
      $(".back-to-top").fadeIn("slow");
    } else {
      $(".back-to-top").fadeOut("slow");
    }
  });
  $(".back-to-top").click(function () {
    $("html, body").animate({ scrollTop: 0 }, 1500, "easeInOutExpo");
    return false;
  });

  function initCarousels() {
    if (typeof $.fn.owlCarousel !== "function") {
      return;
    }

    if ($(".testimonial-carousel").length) {
      $(".testimonial-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 2000,
        center: false,
        dots: true,
        loop: true,
        margin: 25,
        nav: true,
        navText: [
          '<i class="bi bi-arrow-left"></i>',
          '<i class="bi bi-arrow-right"></i>',
        ],
        responsiveClass: true,
        responsive: {
          0: {
            items: 1,
          },
          576: {
            items: 1,
          },
          768: {
            items: 1,
          },
          992: {
            items: 2,
          },
          1200: {
            items: 2,
          },
        },
      });
    }

    if ($(".vegetable-carousel").length) {
      $(".vegetable-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1500,
        center: false,
        dots: true,
        loop: true,
        margin: 25,
        nav: true,
        navText: [
          '<i class="bi bi-arrow-left"></i>',
          '<i class="bi bi-arrow-right"></i>',
        ],
        responsiveClass: true,
        responsive: {
          0: {
            items: 1,
          },
          576: {
            items: 1,
          },
          768: {
            items: 2,
          },
          992: {
            items: 3,
          },
          1200: {
            items: 4,
          },
        },
      });
    }
  }

  $(document).ready(function () {
    initCarousels();
  });

  // Modal Video
  $(document).ready(function () {
    var $videoSrc;
    $(".btn-play").click(function () {
      $videoSrc = $(this).data("src");
    });
    console.log($videoSrc);

    $("#videoModal").on("shown.bs.modal", function (e) {
      $("#video").attr(
        "src",
        $videoSrc + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0",
      );
    });

    $("#videoModal").on("hide.bs.modal", function (e) {
      $("#video").attr("src", $videoSrc);
    });
  });
})(jQuery);

(function () {
  "use strict";

  // Variables
  let qte = 1;
  const quantityWrapper = document.getElementById("productQuantity");
  const idProduit = parseInt(quantityWrapper?.dataset.productId || "1", 10);
  const stock = parseInt(quantityWrapper?.dataset.stock || "0", 10);

  // Fonction pour changer la quantité
  function changerQuantite(delta) {
    const input = document.getElementById("qteInput");
    if (!input) return;

    const maxStock = Number.isFinite(stock) && stock > 0 ? stock : 1;
    let currentQte = parseInt(input.value, 10) || 1;
    currentQte = Math.max(1, Math.min(maxStock, currentQte + delta));
    input.value = currentQte;
    qte = currentQte;
  }

  // Fonction pour ajouter au panier
  function ajouterAuPanier() {
    const btn = document.getElementById("btnAjouter");
    const quantite = document.getElementById("qteInput").value;
    const messageDiv = document.getElementById("messageAjout");
    const messageErrorDiv = document.getElementById("messageError");
    const messageTexte = document.getElementById("messageTexte");
    const messageErrorTexte = document.getElementById("messageErrorTexte");

    // Cacher les messages précédents
    messageDiv.style.display = "none";
    messageErrorDiv.style.display = "none";

    // Désactiver le bouton
    btn.disabled = true;
    btn.innerHTML =
      '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Ajout en cours...';

    const url = "/panier/ajouter";

    fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body:
        "idProduit=" +
        encodeURIComponent(idProduit) +
        "&quantite=" +
        encodeURIComponent(quantite),
    })
      .then(function (response) {
        if (!response.ok) {
          throw new Error("Erreur serveur: " + response.status);
        }
        return response.json();
      })
      .then(function (data) {
        if (data.success) {
          // Mettre à jour le compteur du panier
          const cartCount = document.querySelector(".cart-count");
          if (cartCount) {
            const nouveauTotal = Number.parseInt(data.nouveauTotal, 10);
            cartCount.textContent = Number.isFinite(nouveauTotal)
              ? nouveauTotal
              : (parseInt(cartCount.textContent, 10) || 0) +
                parseInt(quantite, 10);
          }

          messageTexte.textContent =
            "✅ Produit ajouté au panier ! (" + quantite + "x)";
          messageDiv.style.display = "block";
          messageDiv.className = "alert alert-success mt-3";

          setTimeout(function () {
            messageDiv.style.display = "none";
          }, 4000);
        } else {
          messageErrorTexte.textContent =
            data.message || "Erreur lors de l'ajout au panier";
          messageErrorDiv.style.display = "block";
          messageErrorDiv.className = "alert alert-danger mt-3";

          setTimeout(function () {
            messageErrorDiv.style.display = "none";
          }, 4000);
        }
      })
      .catch(function (error) {
        console.error("Erreur:", error);
        messageErrorTexte.textContent =
          "Erreur de connexion au serveur. Veuillez réessayer.";
        messageErrorDiv.style.display = "block";
        messageErrorDiv.className = "alert alert-danger mt-3";

        setTimeout(function () {
          messageErrorDiv.style.display = "none";
        }, 4000);
      })
      .finally(function () {
        btn.disabled = false;
        btn.innerHTML =
          '<i class="fa fa-shopping-bag me-2 text-primary"></i> Ajouter au panier';
      });
  }

  function changerMode() {
    const selected = document.querySelector(
      'input[name="modeReception"]:checked',
    );
    const blocLivraison = document.getElementById("blocLivraison");
    const blocRetrait = document.getElementById("blocRetrait");
    const frais = document.getElementById("frais");

    if (!selected || !blocLivraison || !blocRetrait || !frais) {
      return;
    }

    if (selected.value === "Livraison_Domicile") {
      blocLivraison.style.display = "block";
      blocRetrait.style.display = "none";
      frais.textContent = "5 000 Ar";
    } else {
      blocLivraison.style.display = "none";
      blocRetrait.style.display = "block";
      frais.textContent = "0 Ar";
    }
  }

  function setupSignupValidation() {
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
        console.error("Erreur de validation :", err);
      }
    }

    inputs.forEach((i) => i.addEventListener("input", validate));
    setTimeout(validate, 100);
  }

  // Initialisation quand le DOM est chargé
  document.addEventListener("DOMContentLoaded", function () {
    // Bouton moins
    const btnMoins = document.getElementById("btnMoins");
    if (btnMoins) {
      btnMoins.addEventListener("click", function () {
        changerQuantite(-1);
      });
    }

    // Bouton plus
    const btnPlus = document.getElementById("btnPlus");
    if (btnPlus) {
      btnPlus.addEventListener("click", function () {
        changerQuantite(1);
      });
    }

    // Bouton ajouter
    const btnAjouter = document.getElementById("btnAjouter");
    if (btnAjouter) {
      btnAjouter.addEventListener("click", ajouterAuPanier);
    }

    // Empêcher la soumission du formulaire par Enter
    const qteInput = document.getElementById("qteInput");
    if (qteInput) {
      qteInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
          e.preventDefault();
        }
      });

      qteInput.addEventListener("input", function () {
        let val = parseInt(this.value, 10) || 1;
        const maxStock = Number.isFinite(stock) && stock > 0 ? stock : 1;
        val = Math.max(1, Math.min(maxStock, val));
        this.value = val;
        qte = val;
      });

      qteInput.addEventListener("change", function () {
        let val = parseInt(this.value, 10) || 1;
        const maxStock = Number.isFinite(stock) && stock > 0 ? stock : 1;
        val = Math.max(1, Math.min(maxStock, val));
        this.value = val;
        qte = val;
      });
    }

    document
      .querySelectorAll('input[name="modeReception"]')
      .forEach(function (radio) {
        radio.addEventListener("change", changerMode);
      });
    changerMode();
    setupSignupValidation();
  });

  // Exposer les fonctions globalement pour les tests
  window.changerQuantite = changerQuantite;
  window.ajouterAuPanier = ajouterAuPanier;
  window.changerMode = changerMode;
})();
