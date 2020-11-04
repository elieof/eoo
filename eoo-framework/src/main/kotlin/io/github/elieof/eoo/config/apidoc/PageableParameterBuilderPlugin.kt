package io.github.elieof.eoo.config.apidoc

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import org.springframework.data.domain.Pageable
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.schema.ScalarType
import springfox.documentation.service.ParameterType
import springfox.documentation.service.RequestParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.OperationBuilderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import java.util.*

public open class PageableParameterBuilderPlugin(resolver: TypeResolver) : OperationBuilderPlugin {

    public companion object {
        /** Constant `DEFAULT_PAGE_NAME="page"`  */
        public const val DEFAULT_PAGE_NAME: String = "page"

        /** Constant `PAGE_DESCRIPTION="Page number of the requested page"`  */
        public const val PAGE_DESCRIPTION: String = "Page number of the requested page"

        /** Constant `DEFAULT_SIZE_NAME="size"`  */
        public const val DEFAULT_SIZE_NAME: String = "size"

        /** Constant `SIZE_DESCRIPTION="Size of a page"`  */
        public const val SIZE_DESCRIPTION: String = "Size of a page"

        /** Constant `DEFAULT_SORT_NAME="sort"`  */
        public const val DEFAULT_SORT_NAME: String = "sort"

        /** Constant `SORT_DESCRIPTION="Sorting criteria in the format: propert"{trunked}`  */
        public const val SORT_DESCRIPTION: String = """Sorting criteria in the format: property(,asc|desc).
            | Default sort order is ascending. Multiple sort criteria are supported."""

    }

    private val pageableType: ResolvedType = resolver.resolve(Pageable::class.java)

    override fun supports(delimiter: DocumentationType): Boolean {
        return DocumentationType.OAS_30 == delimiter
    }

    override fun apply(context: OperationContext) {
        val parameters: MutableList<RequestParameter> = ArrayList()
        for (methodParameter in context.parameters) {
            val resolvedType = methodParameter.parameterType
            if (pageableType == resolvedType) {
                parameters.add(createPageParameter())
                parameters.add(createSizeParameter())
                parameters.add(createSortParameter())
                context.operationBuilder().requestParameters(parameters)
            }
        }
    }

    /**
     * Page name may be varied.
     * See [org.springframework.data.web.PageableHandlerMethodArgumentResolver.setPageParameterName]
     *
     * @return The page parameter name
     */
    protected fun getPageName(): String {
        return DEFAULT_PAGE_NAME
    }

    /**
     * Size name may be varied.
     * See [org.springframework.data.web.PageableHandlerMethodArgumentResolver.setSizeParameterName]
     *
     * @return The size parameter name
     */
    protected fun getSizeName(): String {
        return DEFAULT_SIZE_NAME
    }

    /**
     * Sort name may be varied.
     * See [org.springframework.data.web.SortHandlerMethodArgumentResolver.setSortParameter]
     *
     * @return The sort parameter name
     */
    protected fun getSortName(): String {
        return DEFAULT_SORT_NAME
    }

    /**
     * Create a page parameter.
     * Override it if needed. Set a default value for example.
     *
     * @return The page parameter
     */
    protected fun createPageParameter(): RequestParameter {
        return RequestParameterBuilder()
            .name(getPageName())
            .`in`(ParameterType.QUERY)
            .query {
                it.model { m -> m.scalarModel(ScalarType.INTEGER) }
            }
            .description(PAGE_DESCRIPTION)
            .build()
    }

    /**
     * Create a size parameter.
     * Override it if needed. Set a default value for example.
     *
     * @return The size parameter
     */
    protected fun createSizeParameter(): RequestParameter {
        return RequestParameterBuilder()
            .name(getSizeName())
            .`in`(ParameterType.QUERY)
            .query {
                it.model { m -> m.scalarModel(ScalarType.INTEGER) }
            }
            .description(SIZE_DESCRIPTION)
            .build()
    }

    /**
     * Create a sort parameter.
     * Override it if needed. Set a default value or further description for example.
     *
     * @return The sort parameter
     */
    protected fun createSortParameter(): RequestParameter {
        return RequestParameterBuilder()
            .name(getSortName())
            .`in`(ParameterType.QUERY)
            .query {
                it.model { m ->
                    m.collectionModel { cm ->
                        cm.model { m2 -> m2.scalarModel(ScalarType.STRING) }
                    }
                }
            }
            .description(SORT_DESCRIPTION)
            .build()
    }
}
