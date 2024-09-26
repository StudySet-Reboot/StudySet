/************************************
 *         Toast 메시지 모듈
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

