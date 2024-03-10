# JIPipe Launcher

Launcher application for JIPipe.

https://www.jipipe.org/

Zoltán Cseresnyés, Ruman Gerst, Marc Thilo Figge

Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge\
https://www.leibniz-hki.de/en/applied-systems-biology.html \
HKI-Center for Systems Biology of Infection\
Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)\
Adolf-Reichwein-Straße 23, 07745 Jena, Germany

The project code is licensed under MIT.\
See the LICENSE file provided with the code for the full license.

## Project structure

The project consists of following parts:

* **JIPipe-Launcher-Common** provides all common functionality
* **JIPipe-Launcher-App** contains code for the application
* **JIPipe-Launcher-WebStarter** is the application that is downloaded on the website and does the launcher setup

## Building JIPipe Launcher

You will need following packages:

* Java 8 (newer versions do **not** work until supported by SciJava)
* Maven (please make sure Maven runs with Java 8)
