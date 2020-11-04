package io.github.elieof.eoo.config.apidoc.customizer

import springfox.documentation.spring.web.plugins.Docket

fun interface SpringfoxCustomizer {

    /**
     * Customize the Springfox Docket.
     *
     * @param docket the Docket to customize
     */
    fun customize(docket: Docket)
}
