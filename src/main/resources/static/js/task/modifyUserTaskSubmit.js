document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('modifyTaskForm');

    if (form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const formData = new FormData(form);

            fetch('/groups/task/modifyTask', {
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
                    alert("과제 수정이 완료되었습니다.");
                    console.log('Server response:', data);
                    const redirectUrl = `/groups/${data.groupId}/${data.taskId}/${data.userId}/userTask`;
                    window.location.href = redirectUrl;
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert("문제가 발생했습니다: " + error.message);
                });
        });
    }
});
