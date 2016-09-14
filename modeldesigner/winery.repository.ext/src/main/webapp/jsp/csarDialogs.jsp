<%--

    Copyright 2016 [ZTE] and others.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script>
/**
 * Displays a nice message box with "yes" and "no"
 *
 * TODO: currently cannot be recursively called
 *
 * @param msg      Message to display
 * @param fnOnYes  function to be called if user presses "yes"
 * @param title    (optional) title of the dialog
 */
function cConfirmYesNo(msg, fnOnYes, title) {
	title = title || "Please confirm";
	$("#deployDiv").hide();
	$("#diagyesnotitle").text(title);
	$("#diagyesnomsg").text(msg);
	$("#diagyesnoyesbtn").off("click");
	$("#diagyesnoyesbtn").on("click", function() {
		fnOnYes();
		$("#diagyesnotitle").text("Deploy");
		$("#deployDiv").show();
		$("#diagclose").hide();
		$("#diagyesnomsg").hide();
		$("#diagyesnonobtn").hide();
		$("#diagyesnoyesbtn").hide();
		
		/* var diag = $("#diagyesno");
		// quick hack to get fnOnYes() working -> use the hidden.bs.modal event
		diag.on("hidden.bs.modal", function() {
			fnOnYes();
			diag.off("hidden.bs.modal");
			$("#diagyesnotitle").text("Deploy");
			$("#deployDiv").show();
			$("#diagyesnomsg").hide();
			$("#diagyesnonobtn").hide();
			$("#diagyesnoyesbtn").hide();
		});  */
		//diag.modal("hide");
	});
	$("#diagyesno").modal("show");
}

function restDialog()
{
	$("#diagyesno").modal("hide");
	$("#diagclose").show();
	$("#diagyesnomsg").show();
	$("#diagyesnonobtn").show();
	$("#diagyesnoyesbtn").show();
}

/*$(function() {
	$("#diagyesno").on("shown.bs.modal", function() {
		$("#diagyesnoyesbtn").focus();
	});
});*/
</script>

<div class="modal fade z1051" id="diagyesno">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button id="diagclose" type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="diagyesnotitle"></h4>
			</div>
			<div class="modal-body">
				<p id="diagyesnomsg"></p>
				<div id="deployDiv" style="text-align: center;display: none;" >
				    <img alt="" src="../images/deploying.gif">
				    <br/>
				    <label style="font-weight: normal; margin-bottom: 0px;">Deploying···</label>
				</div>
			</div>
			<div class="modal-footer">
				<button id="diagyesnonobtn" type="button" class="btn btn-default" data-dismiss="modal">No</button>
				<button id="diagyesnoyesbtn" type="button" class="btn btn-primary">Yes</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade z1060" id="diagmessage">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="diagmessagetitle"></h4>
			</div>
			<div class="modal-body" id="diagmessagemsg">
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
			</div>
		</div>
	</div>
</div>
