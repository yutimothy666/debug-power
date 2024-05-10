package io.github.future0923.debug.power.idea.listener.event;

import io.github.future0923.debug.power.idea.ui.convert.ConvertType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author future0923
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConvertDataEvent extends DataEvent {

    private ConvertType convertType;

}
