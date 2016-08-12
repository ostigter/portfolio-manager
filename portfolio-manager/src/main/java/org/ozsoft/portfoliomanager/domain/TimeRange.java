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

package org.ozsoft.portfoliomanager.domain;

import java.util.Calendar;
import java.util.Date;

/**
 * Time range.
 * 
 * @author Oscar Stigter
 */
public enum TimeRange {

    TEN_YEAR(Calendar.YEAR, -10, 10),

    FIVE_YEAR(Calendar.YEAR, -5, 5),

    THREE_YEAR(Calendar.YEAR, -3, 3),

    ONE_YEAR(Calendar.YEAR, -1, 1),

    ONE_MONTH(Calendar.MONTH, -1, 0),

    FIVE_DAY(Calendar.DATE, -5, 0),

    ONE_DAY(Calendar.DATE, -1, 0),

    ;

    private final int field;

    private final int delta;

    private final int duration;

    private TimeRange(int field, int delta, int duration) {
        this.field = field;
        this.delta = delta;
        this.duration = duration;
    }

    public Date getFromDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(field, delta);
        return cal.getTime();
    }

    public int getDuration() {
        return duration;
    }
}
