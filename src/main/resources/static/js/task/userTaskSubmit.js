document.addEventListener("DOMContentLoaded", function() {

    // 과제 제출
    document.querySelector('.taskSubmit form').addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = new FormData(this);
        const taskId = document.getElementById('taskId').value;
        const groupId = document.getElementById('groupId').value;
        const url = `/groups/${groupId}/${taskId}/taskDetail`;

        fetch('/groups/task/submitTask', {
            method: 'POST',
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
                console.log(data);

                const redirectUrl = data.redirectUrl;
                window.location.href = redirectUrl;
            })
            .catch(error => {
                alert("문제가 발생했습니다: " + error.message);
            });
    });

});