
package musicloud.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="copyright", url="${api.url.copyright}")
public interface CopyrightService {

    @RequestMapping(method= RequestMethod.POST, path="/copyrights/{id}")
    public void approve(@PathVariable("id") Long copyrightId, @RequestBody Copyright copyright);

}
