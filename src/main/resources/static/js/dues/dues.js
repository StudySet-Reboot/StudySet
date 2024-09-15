document.addEventListener("DOMContentLoaded", function() {
    var btn = document.getElementById("create-modal-btn");
    var createModal = document.getElementById("create-modal");
    var closeButtons = document.querySelectorAll(".close-btn");

    btn.onclick = function() {
        createModal.style.display = "block";
    }

    closeButtons.forEach(function(btn) {
        btn.onclick = function() {
            btn.parentElement.parentElement.style.display = "none";
        }
    });

    window.onclick = function(event) {
        if (event.target == createModal) {
            createModal.style.display = "none";
        }
    }
});