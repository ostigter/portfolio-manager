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

package org.ozsoft.portfoliomanager.services.downloader;

import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Abstract base class for all quote downloaders.
 * 
 * @author Oscar Stigter
 */
public abstract class QuoteDownloader {

    protected final HttpPageReader httpPageReader;

    /**
     * Constructor.
     * 
     * @param httpPageReader
     *            The HTTP page reader.
     */
    public QuoteDownloader(HttpPageReader httpPageReader) {
        this.httpPageReader = httpPageReader;
    }

    /**
     * Updates a stock. <br />
     * <br />
     * 
     * A stock is considered updated if the price has changed.
     * 
     * @param stock
     *            The stock.
     * 
     * @return {@code true} if the stock was updated, otherwise {@code false}.
     */
    public abstract boolean updateStock(Stock stock);
}
