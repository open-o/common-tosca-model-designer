/*
 * Copyright 2016 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var softwareRepositoryUtil = {};






softwareRepositoryUtil.showStatus=function(status){
   if(status === '1'){
      return " <span class='label label-success'>"+ $.i18n.prop("winery-softwareRepository-status-normal") +"</span>";
   }
   else if(status === '0'){
      return " <span class='label label-warning'>"+ $.i18n.prop("winery-softwareRepository-status-approving") +"</span>";
   }
   else if(status === '2'){
      return " <span class='label label-danger'>"+ $.i18n.prop("winery-softwareRepository-status-unapprove") +"</span>";
   }
   else {
      return " <span class='label label-info'>"+ $.i18n.prop("winery-softwareRepository-status-unknown") +"</span>";
   }


}



$.fn.dataTableExt.oApi.fnPagingInfo = function (oSettings) {
    return {
        "iEnd": oSettings.fnDisplayEnd(),
        "iLength": oSettings._iDisplayLength,
        "iTotal": oSettings.fnRecordsTotal(),
        "iFilteredTotal": oSettings.fnRecordsDisplay(),
        "iPage": Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength),
        "iTotalPages": Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength)
    };
};

$.extend($.fn.dataTableExt.oPagination, {
    "bootstrap_extended": {
        "fnInit": function (oSettings, nPaging, fnDraw) {
            var oLang = oSettings.oLanguage.oPaginate;
            var oPaging = oSettings.oInstance.fnPagingInfo();

            var fnClickHandler = function (e) {
                e.preventDefault();
                if (oSettings.oApi._fnPageChange(oSettings, e.data.action)) {
                    fnDraw(oSettings);
                }
            };

            $(nPaging).append(
                '<div class="pagination-panel"> ' + oLang.sPage + ' ' +
                '<a href="#" class="btn btn-sm default prev disabled" title="' + oLang.sPrevious + '"><i class="fa fa-angle-left"></i></a>' +
                '<input type="text" class="pagination-panel-input input-mini input-inline input-sm" maxlenght="5" style="text-align:center; margin: 0 4px; border: 1px solid rgb(169, 169, 169);height: 28px;">' +
                '<a href="#" class="btn btn-sm default next disabled" title="' + oLang.sNext + '"><i class="fa fa-angle-right"></i></a> ' +
                oLang.sPageOf + ' <span class="pagination-panel-total"></span>' +
                '</div>'
            );

            var els = $('a', nPaging);

            $(els[0]).bind('click.DT', {action: "previous"}, fnClickHandler);
            $(els[1]).bind('click.DT', {action: "next"}, fnClickHandler);

            $('.pagination-panel-input', nPaging).bind('change.DT', function (e) {
                var oPaging = oSettings.oInstance.fnPagingInfo();
                e.preventDefault();
                var page = parseInt($(this).val());
                if (page > 0 && page < oPaging.iTotalPages) {
                    if (oSettings.oApi._fnPageChange(oSettings, page - 1)) {
                        fnDraw(oSettings);
                    }
                } else {
                    $(this).val(oPaging.iPage + 1);
                }
            });

            $('.pagination-panel-input', nPaging).bind('keypress.DT', function (e) {
                var oPaging = oSettings.oInstance.fnPagingInfo();
                if (e.which == 13) {
                    var page = parseInt($(this).val());
                    if (page > 0 && page < oSettings.oInstance.fnPagingInfo().iTotalPages) {
                        if (oSettings.oApi._fnPageChange(oSettings, page - 1)) {
                            fnDraw(oSettings);
                        }
                    } else {
                        $(this).val(oPaging.iPage + 1);
                    }
                    e.preventDefault();
                }
            });
        },

        "fnUpdate": function (oSettings, fnDraw) {
            var iListLength = 5;
            var oPaging = oSettings.oInstance.fnPagingInfo();
            var an = oSettings.aanFeatures.p;
            var i, j, sClass, iStart, iEnd, iHalf = Math.floor(iListLength / 2);

            if (oPaging.iTotalPages < iListLength) {
                iStart = 1;
                iEnd = oPaging.iTotalPages;
            }
            else if (oPaging.iPage <= iHalf) {
                iStart = 1;
                iEnd = iListLength;
            } else if (oPaging.iPage >= (oPaging.iTotalPages - iHalf)) {
                iStart = oPaging.iTotalPages - iListLength + 1;
                iEnd = oPaging.iTotalPages;
            } else {
                iStart = oPaging.iPage - iHalf + 1;
                iEnd = iStart + iListLength - 1;
            }


            for (i = 0, iLen = an.length; i < iLen; i++) {
                var wrapper = $(an[i]).parents(".dataTables_wrapper");

                if (oPaging.iTotalPages <= 0) {
                    $('.pagination-panel, .dataTables_length', wrapper).hide();
                } else {
                    $('.pagination-panel, .dataTables_length', wrapper).show();
                }

                $('.pagination-panel-total', an[i]).html(oPaging.iTotalPages);
                $('.pagination-panel-input', an[i]).val(oPaging.iPage + 1);

                // Remove the middle elements
                $('li:gt(1)', an[i]).filter(':not(.next)').remove();

                // Add the new list items and their event handlers
                for (j = iStart; j <= iEnd; j++) {
                    sClass = (j == oPaging.iPage + 1) ? 'class="active"' : '';
                    $('<li ' + sClass + '><a href="#">' + j + '</a></li>')
                        .insertBefore($('li.next:first', an[i])[0])
                        .bind('click', function (e) {
                            e.preventDefault();
                            oSettings._iDisplayStart = (parseInt($('a', this).text(), 10) - 1) * oPaging.iLength;
                            fnDraw(oSettings);
                        });
                }

                // Add / remove disabled classes from the static elements
                if (oPaging.iPage === 0) {
                    $('a.prev', an[i]).addClass('disabled');
                } else {
                    $('a.prev', an[i]).removeClass('disabled');
                }

                if (oPaging.iPage === oPaging.iTotalPages - 1 || oPaging.iTotalPages === 0) {
                    $('a.next', an[i]).addClass('disabled');
                } else {
                    $('a.next', an[i]).removeClass('disabled');
                }
            }
        }
    }
});


softwareRepositoryUtil.getQueryString=function(url,name){
 var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
    var r = url.match(reg); 
    if (r != null) return unescape(r[2]); 
    return null; 
      
}

softwareRepositoryUtil.getTableCheckedData = function(tableId) {
    var checkedDatas = {
    	ids: [],
    	projects: []
    };
    var nodes = $("#" + tableId).dataTable().fnGetNodes();
    $.each(nodes, function(index, tr){
        var checked = $(tr).find("input:checked");
        if(checked.length) {
            var id = checked.val();
            checkedDatas.ids.push(id);
            var project = checked.data("project");
            checkedDatas.projects.push(project);
        }
    });
    return checkedDatas;
}

softwareRepositoryUtil.loadi18n = function(fileNamePrefix, filePath, lang){
    var properties = {
        name: fileNamePrefix,
        path: filePath,
        mode: 'map',
        callback: function() {
            var i18nItems = $("[name_i18n=winery_i18n]");
            for(var i=0;i<i18nItems.length;i++) {
                var $item = $(i18nItems.eq(i));
                var itemId = $item.attr("id");
                var itemTitle = $item.attr("title");
                var itemPlaceholder = $item.attr("placeholder");
                if(typeof(itemTitle) != "undefined") {
                    $item.attr("title", $.i18n.prop(itemTitle));
                } else if(typeof(itemPlaceholder) != "undefined") {
                    $item.attr("placeholder", $.i18n.prop(itemPlaceholder));
                } else {
                    $item.text($.i18n.prop(itemId));
                }
            }
        }
    };
    if(!lang) {
        var func = window.top.getLanguage;
        if(func) {
            lang = func();
        }
    }
    if(lang) {
        properties.language = lang;
    }

    jQuery.i18n.properties(properties);
}