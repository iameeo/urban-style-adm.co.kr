<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<script type="text/javascript" src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
<form action="http://localhost:9090/upload.do" id="form" name="form" method="POST" enctype="multipart/form-data">
    <div class="form-group">
        <select name="port">
            <option value="9998">PROTOTYPE-1</option>
            <option value="9999">PROTOTYPE-2</option>
        </select>
        <input type="file" id="uploadFile" name="uploadFile" capture="camera" accept="image/*" class="btn btn-default btn-lg">
        <input type="button" class="btn_upfile" value="submit">
    </div>
</form>

<ul class="result"></ul>

<script>

    $(function(){
        // form 값 전부 보내기
        $('.btn_upfile').click(function(){
        	
        	
        	
        	var form = $('#form')[0];

            // FormData 객체 생성
            var formData = new FormData(form);

            // 코드로 동적으로 데이터 추가 가능.
//                        formData.append("userId", "testUser!");

            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: "/upload.do",
                data: formData,
                processData: false,
                contentType: false,
                cache: false,
                timeout: 600000,
                success: function (data) {
                	$(".result").empty();
                	var jbSplit = data.split(',');
                    for ( var i in jbSplit ) {
                    	$(".result").append("<li>"+ jbSplit[i] +"</li>");
                      //$("body").append("<iframe src='https://www.google.co.kr/search?q="+ jbSplit[i] +"&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjtytPa9IjiAhXGdd4KHVy9AfsQ_AUIECgD&cshid=1557214679339475&biw=2560&bih=1337'></iframe>");
                    }
                	
                    //console.log("SUCCESS : ", data);
                },
                error: function (e) {
                    console.log("ERROR : ", e);
                }
            });
        });
    });

</script>