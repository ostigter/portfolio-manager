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
 * Stock credit rating from S&P, Moody's and/or Fitch.
 * 
 * @author Oscar Stigter
 */
public enum CreditRating {

    /** AAA, the highest rating. */
    AAA("AAA"),

    /** AA+ (investment grade). */
    AA_PLUS("AA+"),

    /** AA (investment grade). */
    AA("AA"),

    /** AA- (investment grade). */
    AA_MINUS("AA-"),

    /** A+ (investment grade). */
    A_PLUS("A+"),

    /** A (investment grade). */
    A("A"),

    /** A- (investment grade). */
    A_MINUS("A-"),

    /** BBB+ (investment grade). */
    BBB_PLUS("BBB+"),

    /** BBB (investment grade). */
    BBB("BBB"),

    /** BBB- (lowest investment-grade rating). */
    BBB_MINUS("BBB-"),

    /** BB+ (highest non-investment-grade rating). */
    BB_PLUS("BB+"),

    /** BB (non-investment grade). */
    BB("BB"),

    /** BB- (non-investment grade). */
    BB_MINUS("BB-"),

    /** B+ (non-investment grade). */
    B_PLUS("B+"),

    /** B (non-investment grade). */
    B("B"),

    /** B- (non-investment grade). */
    B_MINUS("B-"),

    /** CCC (non-investment grade). */
    CCC("CCC"),

    /** CC (non-investment grade). */
    CC("CC"),

    /** C (lowest grade, barely above bankruptcy). */
    C("C"),

    /** Not rated (neutral). */
    NA("N/R"),

    ;

    private String text;

    CreditRating(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static CreditRating parse(String text) {
        for (CreditRating creditRating : CreditRating.values()) {
            if (creditRating.getText().equals(text)) {
                return creditRating;
            }
        }
        return null;
    }
}
