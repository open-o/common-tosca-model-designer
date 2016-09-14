/*
 * Copyright 2016 [ZTE] and others.
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
/**
 * Select2 Thai translation.
 *
 * Author: Atsawin Chaowanakritsanakul <joke@nakhon.net>
 */
(function ($) {
    "use strict";

    $.extend($.fn.select2.defaults, {
        formatNoMatches: function () { return "ไม่พบข้อมูล"; },
        formatInputTooShort: function (input, min) { var n = min - input.length; return "โปรดพิมพ์เพิ่มอีก " + n + " ตัวอักษร"; },
        formatInputTooLong: function (input, max) { var n = input.length - max; return "โปรดลบออก " + n + " ตัวอักษร"; },
        formatSelectionTooBig: function (limit) { return "คุณสามารถเลือกได้ไม่เกิน " + limit + " รายการ"; },
        formatLoadMore: function (pageNumber) { return "กำลังค้นข้อมูลเพิ่ม..."; },
        formatSearching: function () { return "กำลังค้นข้อมูล..."; }
    });
})(jQuery);
