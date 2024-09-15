document.addEventListener("DOMContentLoaded", function() {
    var btn = document.getElementById("create-modal-btn");
    var createModal = document.getElementById("create-modal");
    var closeButtons = document.querySelectorAll(".close-btn");
    var showPaymentBtn = document.getElementById("show-payment-btn");
    var showDuesBtn = document.getElementById("show-dues-btn");

    var groupId = document.getElementById("groupId").value;

    btn.onclick = function() {
        createModal.style.display = "block";
    }

    closeButtons.forEach(function(btn) {
        btn.onclick = function() {
            btn.parentElement.parentElement.style.display = "none";
        }
    });

    showPaymentBtn.onclick = function () {
        location.href=`/groups/${groupId}/payment`;
    }

    showDuesBtn.onclick = function () {
        location.href=`/groups/${groupId}/dues`;
    }

    window.onclick = function(event) {
        if (event.target == createModal) {
            createModal.style.display = "none";
        }
    }
});