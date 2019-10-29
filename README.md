# Portfolio Manager (portfolio-manager)

## Introduction

Portfolio Manager is a desktop application to track stocks and manage your portfolio, especially designed for value and dividend (growth) investing styles based on the U.S. market.

Real-time and historic stock prices are retrieved from Yahoo Finance, while other stock data has to be entered manually.

## Features

* Supports all US stocks (common, preferred, MLPs), ETFs and mutual funds (from hereon refered to as 'stocks')
* Add and remove stocks to track, by default on your 'watch' list
* Promote great stocks to your 'goal' portfolio (owned or on your wish list)
* Demote bad stocks to the 'bench'
* Track your owned stocks (open and/or closed positions) in your portfolio
* Update all stocks with real-time data from MarketWatch, Yahoo Finance, David Fish' CCC list and Morningstar
* Update real-time prices of your owned stocks from Market Watch (less stocks means faster updates), even pre-market or after-hours
* View stock price graphs (10 years, 1 year, 10 days and current/last day, all in one view)
* Manage your transactions (currently BUY, SELL and DIVIDEND)
* Details per stock:
 * ticker symbol (as per MarketWatch or Yahoo Finance)
 * name (user defined)
 * last price
 * change in price since previous closing price, in percentage
 * current P/E ratio
 * target price (for which you would be considering to buy)
 * target price index (target price divided by last price, >= 100 means in buy zone)
 * Morningstar value rating (1 to 5 stars)
 * annual dividend/interest/distribution rate
 * current yield (dividend, interest or distribution)
 * 5-year annualized dividend growth rate
 * number of consecutive years of dividend growth (Dividend Aristocrat/Champion/Contender/Challenger)
 * credit rating (currently manually set by the user)
 * personal notes
* Details per portfolio position:
 * number of owned shares
 * total costbase (including transaction costs)
 * costbase per share
 * current market value
 * relative weight in portfolio in percentage
 * unrealized result (paper gain/loss)
 * annual income (based on current yield)
 * yield-on-cost (current yield divided by costbase)
 * total received income
 * total realized result
 * total return (unrealized result + realized result + received income) 
* Portfolio statistics:
 * costbase
 * market value
 * unrealized result
 * annual income
 * received income
 * realized result
 * total return
* View portfolio statistics per month, quarter, year and overall:
 * average costbase (investment)
 * received income
 * total return
* Automatically deduct dividend tax (optionally)
* Analyze all stocks based on 10-, 5- and 1-year CAGR and 1-year discount (price drop), exporting to CSV file
* Stores all data in a single (plain text) JSON file on the file system (data/portfolio.json)
* Writes to an application log file for diagnostics (log/portfolio-manager.log)

## Installation

The application runs on any PC or device supporting Java 8, in the form of a single, executable JAR file.

## Development

Portfolio Manager is implemented in Java SE (JDK 8) with a Swing GUI and has a Maven project structure.

The open source project is archived on GitHub:
https://github.com/ostigter/portfolio-manager/

## License

It is licensed under the Apache License version 2.0, so the code may be freely distributed and (re)used. See the LICENSE.txt file for details.
