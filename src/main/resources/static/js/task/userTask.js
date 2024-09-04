document.addEventListener('DOMContentLoaded', function() {
    const deleteButton = document.getElementById('deleteTaskButton');

    if (deleteButton) {
        deleteButton.addEventListener('click', function(event) {
            event.preventDefault();

            // 사용자에게 삭제 확인 메시지 표시
            const confirmation = confirm("정말로 이 과제를 삭제하시겠습니까?");

            if (confirmation) {
                // 사용자가 확인을 클릭한 경우에만 삭제 요청을 보냄
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
            } else {
                // 사용자가 취소를 클릭한 경우 아무 작업도 하지 않음
                alert("과제 삭제를 취소하였습니다.");
            }
        });
    }
});
