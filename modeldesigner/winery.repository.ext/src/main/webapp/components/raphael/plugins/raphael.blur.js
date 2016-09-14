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
(function () {
    if (Raphael.vml) {
        var reg = / progid:\S+Blur\([^\)]+\)/g;
        Raphael.el.blur = function (size) {
            var s = this.node.style,
                f = s.filter;
            f = f.replace(reg, "");
            if (size != "none") {
                s.filter = f + " progid:DXImageTransform.Microsoft.Blur(pixelradius=" + (+size || 1.5) + ")";
                s.margin = Raphael.format("-{0}px 0 0 -{0}px", Math.round(+size || 1.5));
            } else {
                s.filter = f;
                s.margin = 0;
            }
        };
    } else {
        var $ = function (el, attr) {
            if (attr) {
                for (var key in attr) if (attr.hasOwnProperty(key)) {
                    el.setAttribute(key, attr[key]);
                }
            } else {
                return doc.createElementNS("http://www.w3.org/2000/svg", el);
            }
        };
        Raphael.el.blur = function (size) {
            // Experimental. No WebKit support.
            if (size != "none") {
                var fltr = $("filter"),
                    blur = $("feGaussianBlur");
                fltr.id = "r" + (Raphael.idGenerator++).toString(36);
                $(blur, {stdDeviation: +size || 1.5});
                fltr.appendChild(blur);
                this.paper.defs.appendChild(fltr);
                this._blur = fltr;
                $(this.node, {filter: "url(#" + fltr.id + ")"});
            } else {
                if (this._blur) {
                    this._blur.parentNode.removeChild(this._blur);
                    delete this._blur;
                }
                this.node.removeAttribute("filter");
            }
        };
    }
})();