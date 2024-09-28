/************************************
 *         Toast & Confirm 모듈
 * ************************************/
export function displayToast(message){
    const toast = $('#toast');
    toast.text(message);
    toast.addClass('show');
    setTimeout(() => {
        toast.removeClass('show');
    }, 3000);
}


export function displayErrorToast(message){
    const toast = $('#error-toast');
    toast.text(message);
    toast.addClass('show');
    setTimeout(() => {
        toast.removeClass('show');
    }, 3000);
}

export function displayErrorToastWithValidation(validationErrors) {
    const toast = $('#error-toast');
    let message = '';
    for (const [field, error] of Object.entries(validationErrors)) {
        message += `${error} `;
    }
    displayErrorToast(message);
}


export function showConfirm(callback) {
    const modal = document.getElementById("confirm-modal");
    const confirmYes = document.getElementById("confirmYes");
    const confirmNo = document.getElementById("confirmNo");
    const closeBtn = document.querySelector(".close-btn");

    modal.style.display = "block";

    // '네' 버튼 클릭 시
    confirmYes.onclick = function() {
        callback(true);
        modal.style.display = "none";

    };

    // '아니오' 버튼 클릭 시
    confirmNo.onclick = function() {
        callback(false);
        modal.style.display = "none";
    };

    // X 버튼 클릭 시
    closeBtn.onclick = function() {
        modal.style.display = "none";
    };

    // 모달 외부 클릭 시
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    };
}


