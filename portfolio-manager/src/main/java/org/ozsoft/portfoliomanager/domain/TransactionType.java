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

/**
 * Transaction type.
 * 
 * @author Oscar Stigter
 */
public enum TransactionType {

    /** Buy shares. */
    BUY("Buy"),

    /** Sell shares. */
    SELL("Sell"),

    /** Receive dividend. */
    DIVIDEND("Dividend"),

    ;

    private final String name;

    /**
     * Constructor.
     * 
     * @param name
     *            The name.
     */
    private TransactionType(String name) {
        this.name = name;
    }

    /**
     * Returns the name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
