document.addEventListener('DOMContentLoaded', function() {
    const deleteButton = document.getElementById('deleteTaskButton');

    if (deleteButton) {
        deleteButton.addEventListener('click', function(event) {
            event.preventDefault();

            fetch(`/groups/task/${taskId}/${userId}/deleteTask`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        // 삭제 성공 시 리다이렉트
                        alert("과제를 삭제하였습니다.");
                        window.location.href = `/groups/${groupId}/${taskId}/taskDetail`;
                    } else {
                        // 오류 처리
                        return response.text().then(text => {
                            throw new Error(text);
                        });
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('과제 삭제에 실패했습니다: ' + error.message);
                });
        });
    }
});
