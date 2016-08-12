// This file is part of the 'portfolio-manager' (Portfolio Manager)
// project, an open source stock portfolio manager application
// written in Java.
//
// Copyright 2015 Oscar Stigter
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ozsoft.portfoliomanager.util;

public class HtmlGrabber {

    private String content;

    public HtmlGrabber(String content) {
        this.content = content;
    }

    public String search(String id) {
        String text = null;
        int p = content.indexOf(id);
        if (p >= 0) {
            content = content.substring(p + 1);
            p = content.indexOf('>');
            if (p >= 0) {
                content = content.substring(p + 1);
                p = content.indexOf('<');
                while (p == 0) {
                    p = content.indexOf('>');
                    if (p >= 0) {
                        content = content.substring(p + 1);
                        p = content.indexOf('<');
                    }
                }
                if (p >= 0) {
                    text = content.substring(0, p).trim();
                    content = content.substring(p);
                }
            }
        }
        return text;
    }
}
