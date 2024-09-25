document.addEventListener("DOMContentLoaded", function() {
    var showPaymentBtn = document.getElementById("show-payment-btn");
    var showDuesBtn = document.getElementById("show-dues-btn");

    var groupId = document.getElementById("groupId").value;

    showPaymentBtn.onclick = function () {
        location.href=`/groups/${groupId}/payments`;
    }

    showDuesBtn.onclick = function () {
        location.href=`/groups/${groupId}/dues`;
    }
});

function deleteItem(itemId, type) {
    var groupId = document.getElementById("groupId").value;
    if(type === "payment"){
        if (confirm("이 항목을 삭제하시겠습니까?")) {

            fetch(`/groups/${groupId}/payments/${itemId}`, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    alert('삭제 성공!');
                } else {
                    alert('삭제에 실패했습니다.');
                }
            }).catch(error => {
                console.error('삭제 요청 실패:', error);
            });
        }
    }
}