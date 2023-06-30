package kumtor.kg.PCMaintanence.service.reference.impl;

import kumtor.kg.PCMaintanence.dto.reference.AssetDto;
import kumtor.kg.PCMaintanence.dto.reference.ManasReferenceDto;
import kumtor.kg.PCMaintanence.service.reference.ManasReferenceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import java.util.List;

@Service
public class ManasReferenceServiceImpl implements ManasReferenceService {

    private final JdbcTemplate jdbcTemplate;

    public ManasReferenceServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ManasReferenceDto getAssetType(String assetCode) {
        String assetTypeQuery = "SELECT TOP 1 T.AssetTypeCode, T.AssetTypeName  FROM TW_AssetType AS T\n" +
                "LEFT JOIN TW_Asset AS A ON T.AssetTypeCode = A.AssetTypeCode \n" +
                "WHERE A.AssetStatusCode <> 'C' AND A.AssetNumber IS NOT NULL AND A.AssetCode = ?";
        return jdbcTemplate.queryForObject(assetTypeQuery, (rs, rowNum) ->
                ManasReferenceDto.builder()
                        .code(rs.getString("AssetTypeCode").stripTrailing())
                        .name(rs.getString("AssetTypeName").stripTrailing())
                        .build(),
                assetCode);
    }

    @Override
    public List<ManasReferenceDto> getAssetTypeReference() {
        String query = "SELECT T.AssetTypeCode, T.AssetTypeName  FROM TW_AssetType AS T \n" +
                "LEFT JOIN TW_Asset AS A ON T.AssetTypeCode = A.AssetTypeCode \n" +
                "WHERE A.AssetStatusCode <> 'C' AND A.AssetNumber IS NOT NULL";

        return jdbcTemplate.query(query, (rs, rowNum) ->
                ManasReferenceDto.builder()
                        .code(rs.getString("AssetTypeCode"))
                        .name(rs.getString("AssetTypeName").trim())
                        .build());
    }

    @Override
    public List<ManasReferenceDto> getWrqReference(String assetCode) {
        if (!ObjectUtils.isEmpty(assetCode)) {

            String query = "SELECT WrqNo, WrqTitle\n" +
                    "FROM TW_Wrq\n" +
                    "WHERE WrqStatusCode <> 70\n" +
                    "AND AssetCode = ?";
            return jdbcTemplate.query(query, (rs, rowNum) ->
                    ManasReferenceDto.builder()
                            .code(rs.getString("WrqNo").stripTrailing())
                            .name(rs.getString("WrqTitle").trim())
                            .build(), assetCode);

        } else {

            String query = "SELECT WrqNo, WrqTitle\n" +
                    "FROM TW_Wrq\n" +
                    "WHERE WrqStatusCode <> 70\n";

            return jdbcTemplate.query(query, (rs, rowNum) ->
                    ManasReferenceDto.builder()
                            .code(rs.getString("WrqNo").stripTrailing())
                            .name(rs.getString("WrqTitle").trim())
                            .build());
        }
    }

    @Override
    public List<AssetDto> getAssetReference() {
        String query = "SELECT AssetCode, AssetName, AssetNumber\n" +
                "FROM TW_Asset\n" +
                "WHERE AssetStatusCode <> 'C' AND AssetNumber IS NOT NULL;";
        return jdbcTemplate.query(query, (rs, rowNum) ->
                AssetDto.builder()
                        .code(rs.getString("AssetCode").stripTrailing())
                        .name(rs.getString("AssetName").trim())
                        .number(rs.getString("AssetNumber").stripTrailing())
                        .build());
    }
}
