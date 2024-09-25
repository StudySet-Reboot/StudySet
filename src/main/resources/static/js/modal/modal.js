function setupModals() {
    const openButtons = document.querySelectorAll(".open-modal-btn");

    openButtons.forEach(function(button) {
        const modalId = button.getAttribute("data-modal");
        const modal = document.getElementById(modalId);
        const closeButtons = modal.querySelectorAll(".close-btn");

        // 모달 열기
        button.onclick = function() {
            modal.style.display = "block";
        };

        // 모달 닫기
        closeButtons.forEach(function(btn) {
            btn.onclick = function() {
                modal.style.display = "none";
            };
        });

        // 모달 외부 클릭 시 닫기
        window.onclick = function(event) {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        };
    });
}

document.addEventListener("DOMContentLoaded", setupModals);