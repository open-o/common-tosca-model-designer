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
 * Select2 <Language> translation.
 * 
 * Author: Your Name <your@email>
 */
(function ($) {
    "use strict";

    $.extend($.fn.select2.defaults, {
        formatNoMatches: function () { return "Δεν βρέθηκαν αποτελέσματα"; },
        formatInputTooShort: function (input, min) { var n = min - input.length; return "Παρακαλούμε εισάγετε " + n + " περισσότερο" + (n == 1 ? "" : "υς") + " χαρακτήρ" + (n == 1 ? "α" : "ες"); },
        formatInputTooLong: function (input, max) { var n = input.length - max; return "Παρακαλούμε διαγράψτε " + n + " χαρακτήρ" + (n == 1 ? "α" : "ες"); },
        formatSelectionTooBig: function (limit) { return "Μπορείτε να επιλέξετε μόνο " + limit + " αντικείμεν" + (limit == 1 ? "ο" : "α"); },
        formatLoadMore: function (pageNumber) { return "Φόρτωση περισσότερων..."; },
        formatSearching: function () { return "Αναζήτηση..."; }
    });
})(jQuery);