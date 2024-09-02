document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('modifyTaskForm');

    if (form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const formData = new FormData(form);

            fetch('/groups/task/modifyTask', {
                method: 'PUT',
                body: formData,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('과제 수정을 완료했습니다.');
                        const redirectUrl = `/groups/${data.groupId}/${data.taskId}/${data.userId}/userTask`;
                        window.location.href = redirectUrl;
                    } else {
                        alert('수정 실패: ' + data.errorMessage);

                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('서버와의 통신 오류가 발생했습니다.');
                });
        });
    }
});
