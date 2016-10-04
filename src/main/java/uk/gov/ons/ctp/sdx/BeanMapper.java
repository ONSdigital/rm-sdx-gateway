package uk.gov.ons.ctp.sdx;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import uk.gov.ons.ctp.sdx.domain.Receipt;
import uk.gov.ons.ctp.sdx.representation.ReceiptDTO;

import javax.inject.Named;

/**
 * The bean mapper to go from Entity objects to Presentation objects.
 */
@Named
public class BeanMapper extends ConfigurableMapper {

  @Override
  public void configureFactoryBuilder(DefaultMapperFactory.Builder builder) {
    builder.compilerStrategy(new EclipseJdtCompilerStrategy());
  }

  /**
   * This method configures the bean mapper.
   *
   * @param factory the mapper factory
   */
  @Override
  protected final void configure(final MapperFactory factory) {
    factory
            .classMap(Receipt.class, ReceiptDTO.class)
            .byDefault()
            .register();
  }
}
