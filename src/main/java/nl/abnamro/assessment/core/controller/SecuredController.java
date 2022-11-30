package nl.abnamro.assessment.core.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import nl.abnamro.assessment.core.config.OpenAPIConfiguration;

@SecurityRequirement(name = OpenAPIConfiguration.SECURITY_SCHEME_BEARER)
public class SecuredController {
}
