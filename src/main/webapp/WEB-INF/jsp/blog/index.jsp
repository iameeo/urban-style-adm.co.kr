<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/include/inc_declare.jsp"%>

<script	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script async defer src="https://apis.google.com/js/api.js"	onload="this.onload=function(){};handleClientLoad()" onreadystatechange="if (this.readyState === 'complete') this.onload()"></script>
<script>
	var GoogleAuth;
	var SCOPE = 'https://www.googleapis.com/auth/blogger';
	function handleClientLoad() {
		gapi.load('client:auth2', initClient);
	}

	function initClient() {
		var discoveryUrl = 'https://www.googleapis.com/discovery/v1/apis/blogger/v3/rest';

		gapi.client.init({
			'apiKey' : 'AIzaSyBSfUi2FJdc2pzt6e99zIJDyEXMrkIb1SU',
			'discoveryDocs' : [ discoveryUrl ],
			'clientId' : '1082435605265-mge45upfekdcjfns5670ft3l5hkjdnvh.apps.googleusercontent.com',
			'scope' : SCOPE
		}).then(function() {
			GoogleAuth = gapi.auth2.getAuthInstance();
			GoogleAuth.isSignedIn.listen(updateSigninStatus);

			var user = GoogleAuth.currentUser.get();
			setSigninStatus();

			$('#sign-in-or-out-button').click(function() {
				handleAuthClick();
			});
			$('#revoke-access-button').click(function() {
				revokeAccess();
			});
		});
	}

	function handleAuthClick() {
		if (GoogleAuth.isSignedIn.get()) {
			GoogleAuth.signOut();
		} else {
			GoogleAuth.signIn();
		}
	}

	function revokeAccess() {
		GoogleAuth.disconnect();
	}

	function setSigninStatus(isSignedIn) {
		var user = GoogleAuth.currentUser.get();
		var isAuthorized = user.hasGrantedScopes(SCOPE);
		if (isAuthorized) {
			$(".googleAuthChk img").addClass("on").parent().attr("href","#");
			//$('#sign-in-or-out-button').html('Sign out');
			//$('#sign-in-or-out-button').html('Sign out');
			//$('#revoke-access-button').css('display', 'inline-block');
		} else {
			//$('#sign-in-or-out-button').html('Sign In/Authorize');
			//$('#revoke-access-button').css('display', 'none');
		}
	}

	function updateSigninStatus(isSignedIn) {
		setSigninStatus();
	}
	
	function commonWrite() {
		$("#content").val(CKEDITOR.instances.content.getData());
		$("#token_g").val(GoogleAuth.currentUser.Ab.Zi.access_token);

		if($("#token_g").val() == "" || $("#token_t").val() == "" || $("#title").val() == "" || $("#content").val() == "" || $("#tag").val() == ""){
			alert("다시 확인해 보세요.");
		}else{
			$("#blogWriteFrm").submit();
		}
	}
</script>
 
<style>
	.contents{width:1200px;}

	.snsLogin img {
	  -webkit-filter: grayscale(100%); /* Safari 6.0 - 9.0 */
	  filter: grayscale(100%);
	}

	.snsLogin img.on {
	  -webkit-filter: grayscale(0%); /* Safari 6.0 - 9.0 */
	  filter: grayscale(0%);
	}
	
	.snsLogin{overflow:hidden;}
	.snsLogin > li {float:left;}
</style>
 
<div>
	<ul class="snsLogin">
		<li><a href="${apiURL_t}"><img class="<c:out value="${ empty access_token_t ? '' : 'on'}" />" style="width:25px;height:25px;" src="data:image/png;base64,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" /></a></li>
		<li class="googleAuthChk"><a href="javascript:handleAuthClick();"><img style="width:25px;height:25px;" src="https://cdn.iconscout.com/icon/free/png-256/google-160-189824.png" /></a></li>
		<li><a href="${apiURL_n}"><img class="<c:out value="${ empty access_token_n ? '' : 'on'}" />" style="width:25px;height:25px;" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAeFBMVEUAxzz///8AxjcAxCZQ0Gjc9N/m9+jR8dbQ9Ntp2YMAxjgAxCwAxTAAxTP7//3p+Osfy01R1HC068Hx/PTL8tVIz2O77cfF7ct/3pXX9d8Awx+L4aA70F+Y5KlJ0mqn6LcszVWh5rB024yH4Jtg2oBa1Xau6bwozVMQgS/cAAACxklEQVR4nO3c7W7aQBBG4Vm7QPGabwqBkBSSpr3/O6yUKi0EbK8NjTKvzvPbWnHixWInks0AAAAAAAAAAAAAAAAAAAAAAAAAQEORJ4kJS8WbrXRLxbdFP8FknjcuFVeTlJXWH5yYfQlphmXTUvnXpIV6zX+rm0ouHE+LhqW8F4Zl0010XxjuGhL9FzbtU/+FYbOtXUqgMNzXPugVCsMuq1lKonAzqllKojA81OxTjcLwWH0XRQr78oXhe+UHVCkMs6q7KFM4sYqfNjKFYV7xPNUprDoqChUuLu9TocKwuvgTXKkwPF3ap1KFF4+KUoVheeF5qlUY7s4TxQrH+7Ojolhh2JzdRLXC85GGXOHZSEOvsPfuJuoVhsNpomDhu6OiYmE/O/5po1h4OtKQLDzZp5qFx0dFzcLjkYZo4dFIQ7Xw3z5VLQzrt08sW/h3pKFb+DbS0C0My1y9MDxH9cLx61FRuTBsSvXC15GGdmHYZ+qF/VK9MByiemGYbdUL+87v4aT5ksPSdeGPRfM1CZd85sKXtOv8Fg63P9ULS0v4KrouHI0e1QstP6gXWt5XL8x26oUWH9QLrdyoF2a7sXihxWf1wtRjruPCwq7dp5+90Mon9UKLa/XCYpp2DvRbaOVQvdDyuXphMbpmn3ootNE1Iw0XhRavGGn4KCyy7kdFH4U2mqkXXjHS8FJoses+dVPYeaThptDivXqhld2Oio4KO440HBV2HGl4KrRt2n8MHRd2Gmm4Kuw00vBVaHGlXthhpOGssMNIw1th+5GGu8Iia7lP3RW2Pir6K2x7VHRYWBStjooOC1vuU4+FFg/qhRZ76oXZL/XCNiMNp4UtRhpeC7N96lHRa6HFO/VCyxNHGh9dWOx6gwSbl7r3ev5ZaZqy0GBQ9TK0/ybxjeWNgTd99zkAAAAAAAAAAAAAAAAAAAAAAAAACb8BlaxF0SY61+4AAAAASUVORK5CYII=" /></a></li>
	</ul>	
	<div style="text-align:right;">
		<select class="text_type">
			<option value="">-</option>
			<option value="emo">이모티콘</option>
			<option value="liv">리브메이트</option>
			<option value="food">맛집</option>
			<option value="review">리뷰</option>
			<option value="dev">개발</option>
		</select>
	</div>
</div>

<div style="padding:10px 0px;">
	<form name="blogWriteFrm" id="blogWriteFrm" action="write.do" method="POST">
		<input type="hidden" name="token_t" id="token_t" value="${access_token_t}" />
		<input type="hidden" name="token_n" id="token_n" value="${access_token_n}" />
		<input type="hidden" name="token_g" id="token_g" value="" />
		<table>
			<tr>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate value="${now}" pattern="yyyy-MM-dd" var="today" />
				<td><input type="text" name="title" id="title" value="" style="width:100%;padding:5px 0px;border:1px solid #d1d1d1;" /><br /></td>
			</tr>
			<tr>
				<td><textarea name="content" id="content"></textarea></td>
			</tr>
			<tr>
				<td><input type="text" name="tag" id="tag" value="" style="width:100%;padding:5px 0px;border:1px solid #d1d1d1;" /><br /></td>
			</tr>
			<tr>
				<td><input type="button" onclick="commonWrite()" value="등록" /></td>
			</tr>
		</table>
	</form>
</div>

<script src="https://cdn.ckeditor.com/4.8.0/full-all/ckeditor.js"></script>
<script>
CKEDITOR.replace( 'content', {
	// Define the toolbar: http://docs.ckeditor.com/ckeditor4/docs/#!/guide/dev_toolbar
	// The full preset from CDN which we used as a base provides more features than we need.
	// Also by default it comes with a 3-line toolbar. Here we put all buttons in a single row.
	toolbar: [
		{ name: 'document', items: [ 'Source', 'CodeSnippet' ] },
		{ name: 'clipboard', items: [ 'Undo', 'Redo' ] },
		{ name: 'styles', items: [ 'Format', 'Font', 'FontSize' ] },
		{ name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'RemoveFormat', 'CopyFormatting' ] },
		{ name: 'colors', items: [ 'TextColor', 'BGColor' ] },
		{ name: 'align', items: [ 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ] },
		{ name: 'links', items: [ 'Link', 'Unlink' ] },
		{ name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote' ] },
		{ name: 'insert', items: [ 'Image', 'Table' ] },
		{ name: 'tools', items: [ 'Maximize' ] },
		{ name: 'editing', items: [ 'Scayt' ] }
	],
	// Since we define all configuration options here, let's instruct CKEditor to not load config.js which it does by default.
	// One HTTP request less will result in a faster startup time.
	// For more information check http://docs.ckeditor.com/ckeditor4/docs/#!/api/CKEDITOR.config-cfg-customConfig
	customConfig: '',
	// Sometimes applications that convert HTML to PDF prefer setting image width through attributes instead of CSS styles.
	// For more information check:
	//  - About Advanced Content Filter: http://docs.ckeditor.com/ckeditor4/docs/#!/guide/dev_advanced_content_filter
	//  - About Disallowed Content: http://docs.ckeditor.com/ckeditor4/docs/#!/guide/dev_disallowed_content
	//  - About Allowed Content: http://docs.ckeditor.com/ckeditor4/docs/#!/guide/dev_allowed_content_rules
	disallowedContent: 'img{width,height,float}',
	extraAllowedContent: 'img[width,height,align]',
	// Enabling extra plugins, available in the full-all preset: http://ckeditor.com/presets-all
	extraPlugins: 'codesnippet',
	codeSnippet_theme: 'monokai_sublime',
	/*********************** File management support ***********************/
	// In order to turn on support for file uploads, CKEditor has to be configured to use some server side
	// solution with file upload/management capabilities, like for example CKFinder.
	// For more information see http://docs.ckeditor.com/ckeditor4/docs/#!/guide/dev_ckfinder_integration
	// Uncomment and correct these lines after you setup your local CKFinder instance.
	// filebrowserBrowseUrl: 'http://example.com/ckfinder/ckfinder.html',
	// filebrowserUploadUrl: 'http://example.com/ckfinder/core/connector/php/connector.php?command=QuickUpload&type=Files',
	/*********************** File management support ***********************/
	// Make the editing area bigger than default.
	height: 800,
	// An array of stylesheets to style the WYSIWYG area.
	// Note: it is recommended to keep your own styles in a separate file in order to make future updates painless.
	contentsCss: [ 'https://cdn.ckeditor.com/4.8.0/full-all/contents.css', '/css/mystyles.css' ],
	// This is optional, but will let us define multiple different styles for multiple editors using the same CSS file.
	bodyClass: 'document-editor',
	// Reduce the list of block elements listed in the Format dropdown to the most commonly used.
	format_tags: 'p;h1;h2;h3;pre',
	// Simplify the Image and Link dialog windows. The "Advanced" tab is not needed in most cases.
	removeDialogTabs: 'image:advanced;link:advanced',
	// Define the list of styles which should be available in the Styles dropdown list.
	// If the "class" attribute is used to style an element, make sure to define the style for the class in "mystyles.css"
	// (and on your website so that it rendered in the same way).
	// Note: by default CKEditor looks for styles.js file. Defining stylesSet inline (as below) stops CKEditor from loading
	// that file, which means one HTTP request less (and a faster startup).
	// For more information see http://docs.ckeditor.com/ckeditor4/docs/#!/guide/dev_styles
	stylesSet: [
		/* Inline Styles */
		{ name: 'Marker', element: 'span', attributes: { 'class': 'marker' } },
		{ name: 'Cited Work', element: 'cite' },
		{ name: 'Inline Quotation', element: 'q' },
		/* Object Styles */
		{
			name: 'Special Container',
			element: 'div',
			styles: {
				padding: '5px 10px',
				background: '#eee',
				border: '1px solid #ccc'
			}
		},
		{
			name: 'Compact table',
			element: 'table',
			attributes: {
				cellpadding: '5',
				cellspacing: '0',
				border: '1',
				bordercolor: '#ccc'
			},
			styles: {
				'border-collapse': 'collapse'
			}
		},
		{ name: 'Borderless Table', element: 'table', styles: { 'border-style': 'hidden', 'background-color': '#E6E6FA' } },
		{ name: 'Square Bulleted List', element: 'ul', styles: { 'list-style-type': 'square' } }
	]
} );

$(".text_type").change(function(e){
	text_type($(this).val());
});

function text_type(type){
	var title = "";
	var content = "";
	var tag = "";
	
	if(type=="liv"){
		title = "국민카드 리브메이트 <c:out value="${today}"/> 정답";
		content = '오늘도 잘 보냈나요?<br />오늘 날씨가 장난이 아니네요...<br />날이 너무 너무 더워요. 불괘지수 조심하세요 ㅎㅎㅎ<br />	<br />	리브메이트 오늘의퀴즈 <c:out value="${today}"/> TODO<br />		<br />		KB금융그룹 통합 멤버십 플랫폼 "리브메이트" 에서 진행하는&nbsp;&nbsp;오늘의 퀴즈의 정답을 알아볼가요~<br />		<br />		<strong>찍지말고 검색하자!</strong><br />		<strong>오늘의 퀴즈!</strong><br />		<br />		<br />		<strong>기간</strong><br />		2019. 7. 1 (월) ~ 2019. 8. 31 (토)<br />		<br />		<strong>대상</strong><br />		Liiv Mate 모든 회원<br />		<br />		<strong>내용</strong><br />		매일 달라지는 퀴즈의 정답을 맞히면 난이도에 따라 10P 또는 15P 지급<br />		<br />		<br />		<br />		<br />		<div class="separator" style="clear: both; text-align: center;">		<a href="https://1.bp.blogspot.com/-4vFS83TdRyA/XTz8XUrn5tI/AAAAAAAA9Ps/NnifF-HKdMMC4VyxmrQ_oXY1tXlbxj31ACLcBGAs/s1600/KakaoTalk_20190728_103439900.jpg" imageanchor="1" style="margin-left: 1em; margin-right: 1em;"><img border="0" data-original-height="960" data-original-width="444" height="640" src="https://1.bp.blogspot.com/-4vFS83TdRyA/XTz8XUrn5tI/AAAAAAAA9Ps/NnifF-HKdMMC4VyxmrQ_oXY1tXlbxj31ACLcBGAs/s640/KakaoTalk_20190728_103439900.jpg" width="296" /></a></div>		<br />		<br />		<br />		매일매일 색다른&nbsp;오늘의 퀴즈<br />		<br />		아래의 문제를 확인하고 정답을 맞혀보세요.<br />		<br />		<br />		<br />		정답은 무엇일가요??<br />		<br />		바로<br />		.<br />		.<br />		.<br />		<br />		<br />		<span style="color: #2ecc71;"><span style="font-size: 20px;">정답 :&nbsp;<strong>TODO</strong></span></span><br />		<br />		오늘도 10P&nbsp;벌고&nbsp;즐거운 하루 보내세요~~';
		tag = "국민카드, 리브메이트, 리브메이트정답, 오늘의퀴즈, 오늘의퀴즈<c:out value="${today}"/>정답, 구레나룻";
	}else if(type=="food"){
		title = "[맛집]TODO역-TODO";
		content = "";
		tag = "맛집, 분당선맛집, 회식, 음식점, 최애맛집, 식당";
	}else if(type=="review"){
		title = "[REVIEW]TODO-TODO";
		content = "";
		tag = "상품리뷰, 제품리뷰";
	}else if(type=="dev"){
		title = "[DEVELOPMENT]TODO-TODO";
		content = "";
		tag = "개발자, HTML, CSS, JAVASCRIPT, JAVA, C#, PHP, AI, NODEJS, PYTHON, GITHUB, JSP, JSTL, ASP, VB, AJAX, JQUERY, REACT, AJAX, CSS3";
	}else if(type=="emo"){
		title = "[카카오톡 이모티콘]TODO-TODO";
		content = "";
		tag = "";
	}else{
		title = "";
		content = "";
		tag = "";
	}
	
	$("#title").val(title);
	CKEDITOR.instances.content.setData(content);
	$("#tag").val(tag);
	
}

</script>