import {displayErrorToast, displayToast} from "../common/toast.js";

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('modifyTaskForm');

    if (form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const formData = new FormData(form);

            fetch('/groups/submission/modifySubmission', {
                method: 'PUT',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('과제 제출에 실패했습니다.');
                    }
                })
                .then(data => {
                    displayToast("과제 수정이 완료되었습니다.");
                    const redirectUrl = `/groups/${data.groupId}/tasks/${data.taskId}/${data.userId}/usertask`;
                    setTimeout(() => window.location.href = redirectUrl, 1000);
                })
                .catch(error => {
                    console.error('Error:', error);
                    displayErrorToast("문제가 발생했습니다: " + error.message);
                });
        });
    }
});
